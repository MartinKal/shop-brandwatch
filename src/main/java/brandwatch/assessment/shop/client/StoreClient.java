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
    private final String processOrderUrl;

    public StoreClient(
            @Value("${store.base-url}") String storeBaseUrl,
            @Value("${store.process-order-url}") String processOrderUrl) {
        this.restTemplate = new RestTemplate();
        this.storeBaseUrl = storeBaseUrl;
        this.processOrderUrl = processOrderUrl;
    }

    public ProcessOrderResult processStockAvailability(ProcessOrderRequest request) {
        try {
            ResponseEntity<ProcessOrderResult> response = restTemplate
                    .postForEntity(
                            storeBaseUrl + processOrderUrl,
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

