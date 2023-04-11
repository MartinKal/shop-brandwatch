package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.client.StoreClient;
import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.CreateOrderResult;
import brandwatch.assessment.shop.dto.StockCheckResult;
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

    public CreateOrderResult createOrder(CreateOrderRequest request) {
        StockCheckResult result = storeClient.processStockAvailability(request);
        if (result.getSuccess()) {
            return new CreateOrderResult(createOrderSuccess(request), "Order completed.");
        } else {
            return new CreateOrderResult(createPendingOrder(request), "Order submitted. The order is pending");
        }
    }

    private Order createOrderSuccess(CreateOrderRequest request) {
        Order order = Order.of(
                request.getItems(),
                STATUS_COMPLETED
        );
        return orderRepository.save(order);
    }

    private Order createPendingOrder(CreateOrderRequest request) {
        Order order = Order.of(
                request.getItems(),
                STATUS_PENDING
        );
        return orderRepository.save(order);
    }
}
