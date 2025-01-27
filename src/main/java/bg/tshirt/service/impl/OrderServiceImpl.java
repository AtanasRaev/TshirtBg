package bg.tshirt.service.impl;

import bg.tshirt.database.dto.*;
import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.entity.Order;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.entity.User;
import bg.tshirt.database.repository.ClothRepository;
import bg.tshirt.database.repository.OrderRepository;
import bg.tshirt.database.repository.UserRepository;
import bg.tshirt.exceptions.BadRequestException;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.OrderService;
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
    private final ClothRepository clothRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClothRepository clothRepository,
                            UserRepository userRepository,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clothRepository = clothRepository;
        this.userRepository = userRepository;

        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void createOrder(OrderDTO orderDTO, UserDTO userDTO) {
        User user = validateUser(userDTO);

        if ((orderDTO.getAddress() == null || orderDTO.getAddress().isBlank()) && (user.getAddress() != null && !user.getAddress().isBlank())) {
            orderDTO.setAddress(user.getAddress());
        } else if (orderDTO.getAddress() == null || orderDTO.getAddress().isBlank()) {
            throw new BadRequestException("Address not found");
        }

        Order order = buildOrder(orderDTO, user);
        List<OrderItem> items = buildOrderItems(orderDTO, order);

        order.setItems(items);
        double totalPrice = calculateTotalPrice(items);
        order.setTotalPrice(totalPrice);

        user.getOrders().add(order);
        order.setUser(user);

        orderRepository.save(order);
    }

    @Override
    public void createOrder(OrderDTO orderDTO) {
        if (orderDTO.getAddress() == null || orderDTO.getAddress().isBlank()) {
            throw new BadRequestException("Address not found");
        }

        Order order = buildOrder(orderDTO, null);
        List<OrderItem> items = buildOrderItems(orderDTO, order);

        order.setItems(items);
        double totalPrice = calculateTotalPrice(items);
        order.setTotalPrice(totalPrice);

        this.orderRepository.save(order);
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
        Order order = new Order();
        order.setAddress(orderDTO.getAddress());
        order.setStatus("pending");
        order.setUser(user);
        return order;
    }

    private List<OrderItem> buildOrderItems(OrderDTO orderDTO, Order order) {
        List<Long> clothIds = orderDTO.getItems().stream()
                .map(OrderItemDTO::getClothId)
                .toList();

        Map<Long, Cloth> clothMap = this.clothRepository.findAllById(clothIds)
                .stream()
                .collect(Collectors.toMap(Cloth::getId, Function.identity()));

        return orderDTO.getItems()
                .stream()
                .map(itemDTO -> {
                    Cloth cloth = clothMap.get(itemDTO.getClothId());
                    if (cloth == null) {
                        throw new NotFoundException("Cloth with id: " + itemDTO.getClothId() + " not found");
                    }

                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setCloth(cloth);
                    item.setPrice(cloth.getPrice());
                    item.setQuantity(itemDTO.getQuantity());
                    order.getItems().add(item);

                    return item;
                })
                .toList();
    }

    private double calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
