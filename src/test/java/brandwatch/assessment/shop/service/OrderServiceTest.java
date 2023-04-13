package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.client.StoreClient;
import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.CreateOrderResult;
import brandwatch.assessment.shop.dto.ProcessOrderRequest;
import brandwatch.assessment.shop.dto.ProcessOrderResult;
import brandwatch.assessment.shop.model.Item;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository repository;

    @Mock
    private StoreClient storeClient;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(repository, storeClient);
    }

    @Test
    public void testCreateOrderSuccess() {
        // Given
        CreateOrderRequest orderRequest = new CreateOrderRequest(List.of(
                new Item("apple", 3),
                new Item("banana", 5)
        ));
        Order pendingOrder = Order.of(orderRequest.getItems(), OrderService.STATUS_PENDING);
        when(repository.save(any(Order.class))).thenReturn(pendingOrder);

        ProcessOrderResult processOrderResult = new ProcessOrderResult(true, UUID.randomUUID().toString());
        when(storeClient.processStockAvailability(any(ProcessOrderRequest.class))).thenReturn(processOrderResult);

        // When
        CreateOrderResult orderResult = orderService.createOrder(orderRequest);

        // Then
        verify(repository, times(2)).save(any(Order.class));
        verify(storeClient).processStockAvailability(any(ProcessOrderRequest.class));
        assertEquals(orderResult.getMessage(), "Order completed.");
        assertEquals(pendingOrder.getStatus(), OrderService.STATUS_COMPLETED);
    }

    @Test
    public void testCreateOrderPending() {
        // Given
        CreateOrderRequest orderRequest = new CreateOrderRequest(List.of(
                new Item("apple", 3),
                new Item("banana", 5)
        ));
        Order pendingOrder = Order.of(orderRequest.getItems(), OrderService.STATUS_PENDING);
        when(repository.save(any(Order.class))).thenReturn(pendingOrder);

        ProcessOrderResult processOrderResult = new ProcessOrderResult(false, UUID.randomUUID().toString());
        when(storeClient.processStockAvailability(any(ProcessOrderRequest.class))).thenReturn(processOrderResult);

        // When
        CreateOrderResult orderResult = orderService.createOrder(orderRequest);

        // Then
        verify(repository).save(any(Order.class));
        verify(storeClient).processStockAvailability(any(ProcessOrderRequest.class));
        assertEquals(orderResult.getMessage(), "Order pending.");
        assertEquals(pendingOrder.getStatus(), OrderService.STATUS_PENDING);
    }

    @Test
    void testRetryPendingOrders() {
        // Given
        Map<String, String> itemsInStock = Map.of("apple", "10");
        Order pendingOrder1 = Order.of(List.of(new Item("apple", 5)), OrderService.STATUS_PENDING);
        Order pendingOrder2 = Order.of(List.of(new Item("apple", 3)), OrderService.STATUS_PENDING);
        List<Order> pendingOrders = List.of(pendingOrder1, pendingOrder2);
        when(repository.findAllPendingForProductId("apple")).thenReturn(pendingOrders);

        when(storeClient.processStockAvailability(any(ProcessOrderRequest.class)))
                .thenReturn(new ProcessOrderResult(true, "ref1"));

        // When
        orderService.retryPendingOrders(itemsInStock);

        // Then
        verify(storeClient, times(2)).processStockAvailability(any(ProcessOrderRequest.class));
        verify(repository).saveAll(pendingOrders);
    }
}
