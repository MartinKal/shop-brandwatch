package brandwatch.assessment.shop.client;

import brandwatch.assessment.shop.dto.CreateOrderRequest;
import brandwatch.assessment.shop.dto.StockCheckResult;
import brandwatch.assessment.shop.exception.OrderCouldNotBeCompleted;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class StoreClient {

    private final RestTemplate restTemplate;
    private final String storeBaseUrl;

    public StoreClient(@Value("${store.base-url}") String storeBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.storeBaseUrl = storeBaseUrl;
    }

    public StockCheckResult processStockAvailability(CreateOrderRequest request) {
        try {
            ResponseEntity<StockCheckResult> response = restTemplate
                    .postForEntity(
                            storeBaseUrl + "/products/process",
                            request, StockCheckResult.class
                    );
            return new StockCheckResult(
                    Objects.requireNonNull(
                            response.getBody()).getSuccess(), response.getBody().getMessage());
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.CONFLICT)
                return new StockCheckResult(false, "Order pending.");
            throw new OrderCouldNotBeCompleted(ex.getMessage());
        }
    }
}

