package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.client.StoreClient;
import brandwatch.assessment.shop.dto.*;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        ProcessOrderRequest processOrderRequest = new ProcessOrderRequest(
                order.getOrderReferenceId(),
                order.getItems(),
                false
        );
        ProcessedOrder result = storeClient.processStockAvailability(processOrderRequest);
        if (result.isCompleted()) {
            setOrderToCompleted(order);
            return new CreateOrderResult("Order completed.");
        } else {
            return new CreateOrderResult("Order pending.");
        }
    }

    public void retryPendingOrders(Map<String, String> itemsInStock) {
        Set<Order> ordersToBeProcessed = orderRepository.findAllPending()
                .stream()
                .filter(order -> order
                        .getItems()
                        .stream()
                        .anyMatch(item -> itemsInStock.containsKey(item.getProductId()))
                )
                .collect(Collectors.toSet());

        ProcessRetriedOrdersRequest request =
                new ProcessRetriedOrdersRequest(
                        ordersToBeProcessed
                                .stream()
                                .map(order -> new OrderData(order.getItems(), order.getOrderReferenceId()))
                                .collect(Collectors.toSet())
                );

        Set<String> completedOrders = storeClient.processRetriedStockAvailability(request)
                .getProcessedOrders()
                .stream()
                .filter(ProcessedOrder::isCompleted)
                .map(ProcessedOrder::getOrderReferenceId)
                .collect(Collectors.toSet());

        Set<String> ordersForChange = ordersToBeProcessed
                .stream()
                .map(Order::getOrderReferenceId)
                .filter(completedOrders::contains)
                .collect(Collectors.toSet());

        for (Order order : ordersToBeProcessed) {
            if (ordersForChange.contains(order.getOrderReferenceId())) {
                order.setStatus(STATUS_COMPLETED);
            }
        }
        orderRepository.saveAll(ordersToBeProcessed);
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
