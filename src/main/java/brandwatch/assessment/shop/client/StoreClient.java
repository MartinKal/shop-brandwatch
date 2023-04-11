package brandwatch.assessment.shop.client;

import brandwatch.assessment.shop.model.LineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StoreClient {

    private final RestTemplate restTemplate;
    private final String storeBaseUrl;

    public StoreClient(@Value("${store.base-url}") String storeBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.storeBaseUrl = storeBaseUrl;
    }

    public boolean checkStockAvailability(List<LineItem> products) {
        ResponseEntity<Boolean> response = restTemplate
                .postForEntity(
                        storeBaseUrl + "/stock/check",
                        products, Boolean.class
                );
        return response.getBody() != null && response.getBody();
    }
}

