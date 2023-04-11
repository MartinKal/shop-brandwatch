package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final String STATUS_PENDING = "pending";

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderRequest request) {
        Order order = Order.of(
                request.getLineItems(),
                STATUS_PENDING
        );
        return orderRepository.save(order);
    }
}
