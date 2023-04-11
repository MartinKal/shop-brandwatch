package brandwatch.assessment.shop.controller;

import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.model.Order;
import brandwatch.assessment.shop.service.OrderService;
import brandwatch.assessment.shop.service.OrderValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderValidationService validationService;

    public OrderController(OrderService orderService, OrderValidationService validationService) {
        this.orderService = orderService;
        this.validationService = validationService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        validationService.validateOrderRequest(createOrderRequest);
        Order savedOrder = orderService.createOrder(createOrderRequest);
        if (savedOrder.getStatus().equals(OrderService.STATUS_COMPLETED)) {
            return ResponseEntity.ok(savedOrder);
        } else {
            return ResponseEntity.accepted().build();
        }
    }

}
