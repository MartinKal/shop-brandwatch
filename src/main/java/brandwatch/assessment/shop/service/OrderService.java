package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.client.StoreClient;
import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.CreateOrderResult;
import brandwatch.assessment.shop.dto.StockCheckResult;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void retryPendingOrders(Map<String, String> itemsInStock) {
        List<Order> completedOrders = new ArrayList<>();
        for (Map.Entry<String, String> pair: itemsInStock.entrySet()) {
            List<Order> pendingOrders = orderRepository.findAllPendingForProductId(pair.getKey());
            for (Order pendingOrder: pendingOrders) {
                StockCheckResult result = storeClient
                        .processStockAvailability(CreateOrderRequest.of(pendingOrder.getItems()));
                if (result.getSuccess()) {
                    pendingOrder.setStatus(STATUS_COMPLETED);
                    completedOrders.add(pendingOrder);
                }
            }
        }
        orderRepository.saveAll(completedOrders);
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
