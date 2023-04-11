package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.client.StoreClient;
import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";

    private final OrderRepository orderRepository;
    private final StoreClient storeClient;

    public OrderService(OrderRepository orderRepository, StoreClient storeClient) {
        this.orderRepository = orderRepository;
        this.storeClient = storeClient;
    }

    public Order createOrder(CreateOrderRequest request) {
        boolean inStock = storeClient.checkStockAvailability(request.getLineItems());
        Order saved;
        if (inStock) {
            saved = createOrderSuccess(request);
        } else {
            saved = createPendingOrder(request);
        }
        return orderRepository.save(saved);
    }

    private Order createOrderSuccess(CreateOrderRequest request) {
        Order order = Order.of(
                request.getLineItems(),
                STATUS_COMPLETED
        );
        return orderRepository.save(order);
    }

    private Order createPendingOrder(CreateOrderRequest request) {
        Order order = Order.of(
                request.getLineItems(),
                STATUS_PENDING
        );
        return orderRepository.save(order);
    }
}
