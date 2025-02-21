package bg.tshirt.service.impl;

import bg.tshirt.database.dto.*;
import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.Order;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.entity.User;
import bg.tshirt.database.repository.ClothingRepository;
import bg.tshirt.database.repository.OrderRepository;
import bg.tshirt.database.repository.UserRepository;
import bg.tshirt.exceptions.BadRequestException;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothingService;
import bg.tshirt.service.OrderService;
import bg.tshirt.utils.PhoneNumberValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ClothingRepository clothRepository;
    private final UserRepository userRepository;
    private final ClothingService clothService;
    private final PhoneNumberValidator phoneNumberValidator;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClothingRepository clothRepository,
                            UserRepository userRepository,
                            ClothingService clothService,
                            PhoneNumberValidator phoneNumberValidator,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clothRepository = clothRepository;
        this.userRepository = userRepository;
        this.clothService = clothService;
        this.phoneNumberValidator = phoneNumberValidator;

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
    }

    @Override
    public void createOrder(OrderDTO orderDTO) {
        Optional<User> optional = this.userRepository.findByEmail(orderDTO.getEmail());
        User user = null;

        if (optional.isPresent()) {
            user = optional.get();
        }

        this.orderRepository.save(buildOrder(orderDTO, user));
    }

    @Override
    public Page<OrderPageDTO> getAllOrdersByStatus(Pageable pageable, String status) {
        return this.orderRepository.findAllByStatus(pageable, status)
                .map(order -> this.modelMapper.map(order, OrderPageDTO.class));
    }

    @Override
    public OrdersDetailsDTO findOrderById(Long id) {
        return this.orderRepository.findById(id)
                .map(order -> this.modelMapper.map(order, OrdersDetailsDTO.class))
                .orElse(null);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Optional<Order> byId = this.orderRepository.findById(id);

        if (byId.isEmpty()) {
            throw new NotFoundException("Order with id: " + id + " was not found");
        }
        Order order = byId.get();

        if (order.getStatus().equals(status)) {
            return false;
        }

        order.setStatus(status);

        if ("confirm".equals(status)) {
            this.clothService.setTotalSales(order.getItems(), status, order.getStatus());
        }

        this.orderRepository.save(order);
        return true;
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
        this.phoneNumberValidator.validateBulgarianPhoneNumber(orderDTO.getPhoneNumber());
        return new Order(
                orderDTO.getFirstName(),
                orderDTO.getLastName(),
                orderDTO.getEmail(),
                orderDTO.getSelectedOffice() != null,
                orderDTO.getDeliveryCost(),
                orderDTO.getFinalPrice(),
                user,
                orderDTO.getSelectedOffice() != null ? orderDTO.getSelectedOffice() :orderDTO.getCity() + " (" + orderDTO.getRegion() + ") " + orderDTO.getAddress().trim(),
                orderDTO.getPhoneNumber(),
                orderDTO.getTotalPrice(),
                "pending"
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
        item.setSize(itemDTO.getSize());
        item.setGender(itemDTO.getGender());
        item.setType(itemDTO.getType());
        item.setPrice(itemDTO.getPrice());
        item.setQuantity(itemDTO.getQuantity());
        return item;
    }
}
