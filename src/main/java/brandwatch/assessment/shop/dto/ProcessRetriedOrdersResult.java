package brandwatch.assessment.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessRetriedOrdersResult {
    List<ProcessedOrder> processedOrders;
}
