package brandwatch.assessment.shop.client;

import brandwatch.assessment.shop.dto.ProcessOrderRequest;
import brandwatch.assessment.shop.dto.ProcessOrderResult;
import brandwatch.assessment.shop.exception.OrderCouldNotBeCompleted;
import org.springframework.beans.factory.annotation.Value;
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

    public ProcessOrderResult processStockAvailability(ProcessOrderRequest request) {
        try {
            ResponseEntity<ProcessOrderResult> response = restTemplate
                    .postForEntity(
                            storeBaseUrl + "/products/process",
                            request, ProcessOrderResult.class
                    );
            return new ProcessOrderResult(
                    Objects.requireNonNull(
                            response.getBody()).getSuccess(), response.getBody().getProductReferenceId());
        } catch (HttpClientErrorException ex) {
            throw new OrderCouldNotBeCompleted(ex.getMessage());
        }
    }
}

