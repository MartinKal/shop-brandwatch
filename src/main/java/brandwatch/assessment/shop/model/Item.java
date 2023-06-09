package brandwatch.assessment.shop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @NonNull
    private String productId;
    @NonNull
    private Integer quantity;
}
