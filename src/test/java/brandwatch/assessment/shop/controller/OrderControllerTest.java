package brandwatch.assessment.shop.controller;

import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.CreateOrderResult;
import brandwatch.assessment.shop.model.Item;
import brandwatch.assessment.shop.service.OrderService;
import brandwatch.assessment.shop.service.OrderValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @Mock
    private OrderValidationService validationService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderService, validationService, null);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testCreateOrder() throws Exception {
        List<Item> items = List.of(
                new Item("item1", 5),
                new Item("item2", 3)
        );
        CreateOrderRequest request = new CreateOrderRequest(items);
        CreateOrderResult expectedResult = new CreateOrderResult("Order completed.");

        when(orderService.createOrder(request)).thenReturn(expectedResult);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order completed."));

        verify(validationService, times(1)).validateOrderRequest(request);
        verify(orderService, times(1)).createOrder(request);
    }
}
