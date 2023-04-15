package brandwatch.assessment.shop.dto;

import brandwatch.assessment.shop.model.Item;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderData {
    private List<Item> items;
    private String orderReferenceId;
}
