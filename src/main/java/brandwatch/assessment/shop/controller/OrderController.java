package brandwatch.assessment.shop.controller;

import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.CreateOrderResult;
import brandwatch.assessment.shop.service.OrderService;
import brandwatch.assessment.shop.service.OrderValidationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderValidationService validationService;

    public OrderController(OrderService orderService, OrderValidationService validationService, RedisTemplate<String, String> redisTemplate) {
        this.orderService = orderService;
        this.validationService = validationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResult> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        validationService.validateOrderRequest(createOrderRequest);
        CreateOrderResult createOrderResult = orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok(createOrderResult);
    }
}
