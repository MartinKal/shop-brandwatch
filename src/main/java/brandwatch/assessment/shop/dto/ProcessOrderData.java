package brandwatch.assessment.shop.dto;

import brandwatch.assessment.shop.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessOrderData {
    private String orderReferenceId;
    List<Item> items;
    boolean retriedOrder;
}
