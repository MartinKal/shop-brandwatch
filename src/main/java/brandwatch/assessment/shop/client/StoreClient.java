package brandwatch.assessment.shop.client;

import brandwatch.assessment.shop.dto.*;
import brandwatch.assessment.shop.exception.OrderCouldNotBeCompleted;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class StoreClient {

    private final RestTemplate restTemplate;
    private final String storeBaseUrl;
    private final String processOrderUrl;
    private final String retryOrderUrl;

    public StoreClient(
            @Value("${store.base-url}") String storeBaseUrl,
            @Value("${store.process-order-url}") String processOrderUrl,
            @Value("${store.retry-order-url}") String retryOrderUrl) {
        this.restTemplate = new RestTemplate();
        this.storeBaseUrl = storeBaseUrl;
        this.processOrderUrl = processOrderUrl;
        this.retryOrderUrl = retryOrderUrl;
    }

    public ProcessedOrder processStockAvailability(ProcessOrderRequest request) {
        try {
            ResponseEntity<ProcessedOrder> response = restTemplate
                    .postForEntity(
                            storeBaseUrl + processOrderUrl,
                            request, ProcessedOrder.class
                    );
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new OrderCouldNotBeCompleted(ex.getMessage());
        }
    }

    public ProcessRetriedOrdersResult processRetriedStockAvailability(ProcessRetriedOrdersRequest request) {
        try {
            ResponseEntity<ProcessRetriedOrdersResult> response = restTemplate
                    .postForEntity(
                            storeBaseUrl + retryOrderUrl,
                            request, ProcessRetriedOrdersResult.class
                    );
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new OrderCouldNotBeCompleted(ex.getMessage());
        }
    }
}

