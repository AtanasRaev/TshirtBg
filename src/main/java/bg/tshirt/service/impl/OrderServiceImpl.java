package bg.tshirt.service.impl;

import bg.tshirt.database.dto.*;
import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.Order;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.entity.User;
import bg.tshirt.database.repository.ClothingRepository;
import bg.tshirt.database.repository.OrderRepository;
import bg.tshirt.database.repository.UserRepository;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothingService;
import bg.tshirt.service.EmailService;
import bg.tshirt.service.OrderService;
import bg.tshirt.utils.PhoneNumberUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ClothingRepository clothRepository;
    private final UserRepository userRepository;
    private final ClothingService clothingService;
    private final EmailService emailService;
    private final PhoneNumberUtils phoneNumberUtils;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClothingRepository clothRepository,
                            UserRepository userRepository,
                            ClothingService clothingService,
                            EmailService emailService,
                            PhoneNumberUtils phoneNumberUtils,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clothRepository = clothRepository;
        this.userRepository = userRepository;
        this.clothingService = clothingService;
        this.emailService = emailService;
        this.phoneNumberUtils = phoneNumberUtils;

        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void createOrder(OrderDTO orderDTO, UserDTO userDTO) {
        User user = validateUser(userDTO);
        Order order = buildOrder(orderDTO, user);

        user.getOrders().add(order);
        order.setUser(user);

        this.orderRepository.save(order);
        this.emailService.sendOrderEmail(order);
    }

    @Override
    public void createOrder(OrderDTO orderDTO) {
        Optional<User> optional = this.userRepository.findByEmail(orderDTO.getEmail());
        User user = null;

        if (optional.isPresent()) {
            user = optional.get();
        }
        Order order = buildOrder(orderDTO, user);

        this.orderRepository.save(order);

        this.emailService.sendOrderEmail(order);
    }

    @Override
    public Page<OrderPageDTO> getAllOrdersByStatus(Pageable pageable, String status) {
        return this.orderRepository.findAllByStatus(pageable, status)
                .map(order -> {
                    OrderPageDTO map = this.modelMapper.map(order, OrderPageDTO.class);
                    map.setQuantity(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
                    map.setCustomer(order.getFirstName() + " " + order.getLastName());
                    return map;
                });
    }

    @Override
    public Page<OrderPageDTO> getAllOrders(Pageable pageable) {
        return this.orderRepository.findAll(pageable)
                .map(order -> {
                    OrderPageDTO map = this.modelMapper.map(order, OrderPageDTO.class);
                    map.setQuantity(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
                    map.setCustomer(order.getFirstName() + " " + order.getLastName());
                    return map;
                });
    }

    @Override
    public OrdersDetailsDTO findOrderById(Long id) {
        return this.orderRepository.findById(id)
                .map(order -> {
                    OrdersDetailsDTO map = this.modelMapper.map(order, OrdersDetailsDTO.class);
                    map.setCustomer(order.getFirstName() + " " + order.getLastName());
                    map.setItems(map.getItems().stream()
                            .peek(itemDTO -> {
                                ClothingDetailsPageDTO byId = this.clothingService.findById(itemDTO.getClothingId());
                                if (byId == null) {
                                    throw new NotFoundException("Clothing with id " + itemDTO.getClothingId() + " not found");
                                }

                                itemDTO.setName(getTypeOnBulgarian(byId) + " " + byId.getName());
                                itemDTO.setModel(byId.getModel());

                            }).toList());

                    return map;
                })
                .orElse(null);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Optional<Order> byId = this.orderRepository.findById(id);

        if (byId.isEmpty()) {
            throw new NotFoundException("Order with id: " + id + " was not found");
        }
        Order order = byId.get();

        if (order.getStatus().equalsIgnoreCase(status)) {
            return false;
        }

        order.setStatus(setStatus(status));

        if ("confirm".equalsIgnoreCase(status)) {
            this.clothingService.setTotalSales(order.getItems());
        }

        this.orderRepository.save(order);
        return true;
    }

    @Override
    public Page<OrderPageDTO> findOrdersByUser(String userEmail, Pageable pageable) {
        return this.orderRepository.findByUserId(userEmail, pageable)
                .map(order -> {
                    OrderPageDTO map = this.modelMapper.map(order, OrderPageDTO.class);
                    map.setQuantity(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
                    map.setCustomer(order.getFirstName() + " " + order.getLastName());
                    return map;
                });
    }

    private String setStatus(String status) {
        switch (status.toLowerCase()) {
            case "confirm" -> {
                return "Confirmed";
            }
            case "reject" -> {
                return "Rejected";
            }
            default -> {
                return "Pending";
            }
        }
    }

    private User validateUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new NotFoundException("User not found");
        }

        return userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Order buildOrder(OrderDTO orderDTO, User user) {
        Order order = setOrderDetails(orderDTO, user);
        List<OrderItem> cart = buildOrderItems(orderDTO, order);

        order.setItems(cart);
        return order;
    }

    private Order setOrderDetails(OrderDTO orderDTO, User user) {
        this.phoneNumberUtils.validateBulgarianPhoneNumber(orderDTO.getPhoneNumber());
        return new Order(
                orderDTO.getFirstName(),
                orderDTO.getLastName(),
                orderDTO.getEmail(),
                orderDTO.getSelectedOffice() != null,
                orderDTO.getDeliveryCost(),
                orderDTO.getFinalPrice(),
                user,
                orderDTO.getSelectedOffice() != null ? orderDTO.getSelectedOffice() : orderDTO.getCity() + " (" + orderDTO.getRegion() + ") " + orderDTO.getAddress().trim(),
                this.phoneNumberUtils.formatPhoneNumber(orderDTO.getPhoneNumber()),
                orderDTO.getTotalPrice(),
                "Pending"
        );
    }

    private List<OrderItem> buildOrderItems(OrderDTO orderDTO, Order order) {
        List<Long> clothesIds = orderDTO.getCart().stream()
                .map(OrderItemDTO::getId)
                .toList();

        Map<Long, Clothing> clothesMap = this.clothRepository.findAllById(clothesIds)
                .stream()
                .collect(Collectors.toMap(Clothing::getId, Function.identity()));

        return orderDTO.getCart()
                .stream()
                .map(itemDTO -> {
                    Clothing clothing = clothesMap.get(itemDTO.getId());

                    if (clothing == null) {
                        throw new NotFoundException("Cloth with id: " + itemDTO.getId() + " not found");
                    }

                    OrderItem item = setOrderItemDetails(order, itemDTO, clothing);
                    order.getItems().add(item);

                    return item;
                })
                .toList();
    }

    private static OrderItem setOrderItemDetails(Order order, OrderItemDTO itemDTO, Clothing cloth) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setClothing(cloth);
        item.setSize(extractSize(itemDTO.getSize()));
        item.setGender(itemDTO.getGender());
        item.setType(extractType(itemDTO.getType()));
        item.setPrice(itemDTO.getPrice());
        item.setQuantity(itemDTO.getQuantity());
        return item;
    }

    private static String extractSize(String input) {
        int index = input.indexOf(' ');

        if (index == -1) {
            index = input.indexOf('(');
        }

        return (index == -1) ? input : input.substring(0, index);
    }

    public static String extractType(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int index = input.indexOf(" (");

        return (index == -1) ? input : input.substring(0, index);
    }

    private static String getTypeOnBulgarian(ClothingDetailsPageDTO byId) {
        String name;
        switch (byId.getType()) {
            case T_SHIRT -> name = "Тениска";
            case KIT -> name = "Комплект";
            case LONG_T_SHIRT -> name = "Блуза с дълъг ръкав";
            case SWEATSHIRT -> name = "Суитчър";
            default -> name = "Къси панталони";
        }
        return name;
    }
}
