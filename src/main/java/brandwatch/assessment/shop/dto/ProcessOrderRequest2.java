package brandwatch.assessment.shop.dto;

import brandwatch.assessment.shop.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessOrderRequest2 {
    private Set<OrderData> orders;
}
