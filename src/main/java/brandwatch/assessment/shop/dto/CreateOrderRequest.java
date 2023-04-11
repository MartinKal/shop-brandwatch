package brandwatch.assessment.shop.dto;

import brandwatch.assessment.shop.model.LineItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CreateOrderRequest {
    @NonNull
    List<LineItem> lineItems;
}
