package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.client.StoreClient;
import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.CreateOrderResult;
import brandwatch.assessment.shop.dto.ProcessOrderRequest;
import brandwatch.assessment.shop.dto.ProcessOrderResult;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Order order = createPendingOrder(request);
        ProcessOrderRequest processOrderRequest = new ProcessOrderRequest(order.getOrderReferenceId(), order.getItems());
        ProcessOrderResult result = storeClient.processStockAvailability(processOrderRequest);
        if (result.getSuccess()) {
            setOrderToCompleted(order);
            return new CreateOrderResult("Order completed.");
        } else {
            return new CreateOrderResult("Order pending.");
        }
    }

    public void retryPendingOrders(Map<String, String> itemsInStock) {
        List<Order> completedOrders = new ArrayList<>();
        Set<String> processedOrders = new HashSet<>();

        for (Map.Entry<String, String> pair: itemsInStock.entrySet()) {
            List<Order> pendingOrders = orderRepository.findAllPendingForProductId(pair.getKey());

            for (Order pendingOrder: pendingOrders) {
                if (!processedOrders.contains(pendingOrder.getOrderReferenceId())) {
                    processedOrders.add(pendingOrder.getOrderReferenceId());

                    ProcessOrderResult result = storeClient
                            .processStockAvailability(
                                    new ProcessOrderRequest(
                                            pendingOrder.getOrderReferenceId(),
                                            pendingOrder.getItems()
                                    )
                            );

                    if (result.getSuccess()) {
                        pendingOrder.setStatus(STATUS_COMPLETED);
                        completedOrders.add(pendingOrder);
                    }
                }
            }
        }
        orderRepository.saveAll(completedOrders);
    }

    private Order setOrderToCompleted(Order order) {
        order.setStatus(STATUS_COMPLETED);
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
