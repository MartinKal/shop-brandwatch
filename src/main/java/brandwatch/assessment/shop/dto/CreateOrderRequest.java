package brandwatch.assessment.shop.dto;

import brandwatch.assessment.shop.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NonNull
    List<Item> items;
}
