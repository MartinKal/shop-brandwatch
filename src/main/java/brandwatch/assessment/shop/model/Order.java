package brandwatch.assessment.shop.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "orders")
@CompoundIndex(name = "status_lineItems_idx", def = "{'status': 1, 'lineItems.productName': 1, 'lineItems.quantity': 1}")
@Data
@NoArgsConstructor
public class Order {
    @Id
    private ObjectId id;
    @NonNull
    private List<LineItem> lineItems;
    @NonNull
    private String status;
    @Setter(AccessLevel.NONE)
    private Instant createdDate;
    @Setter(AccessLevel.NONE)
    private String orderReferenceId;

    private Order(List<LineItem> lineItems, String status) {
        this.lineItems = lineItems;
        this.status = status;
        createdDate = Instant.now();
        orderReferenceId = UUID.randomUUID().toString();
    }

    public static Order of(List<LineItem> lineItems, String status) {
        return new Order(lineItems, status);
    }
}
