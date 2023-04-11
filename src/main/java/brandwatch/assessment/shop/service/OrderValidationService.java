package brandwatch.assessment.shop.service;

import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.exception.IllegalCreateOrderRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderValidationService {

    public void validateOrderRequest(CreateOrderRequest request) {
        if (request.getItems().size() < 1) {
            throw new IllegalCreateOrderRequest("Orders need to contain at least 1 line item.");
        }

        request.getItems().forEach(lineItem -> {
            if (lineItem.getProductId().isBlank()) {
                throw new IllegalCreateOrderRequest("Product name cannot be blank.");
            }

            if (lineItem.getQuantity() < 1) {
                throw new IllegalCreateOrderRequest("Quantity should be at least 1.");
            }
        });
    }
}
