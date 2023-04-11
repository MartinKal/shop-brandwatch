package brandwatch.assessment.shop.dto;

import brandwatch.assessment.shop.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResult {
    private Order order;
    private String message;
}
