package brandwatch.assessment.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessOrderResult2 {
    List<ProcessedOrder> processedOrders;
}
