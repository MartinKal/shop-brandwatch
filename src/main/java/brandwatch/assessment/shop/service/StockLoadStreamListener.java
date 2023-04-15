package brandwatch.assessment.shop.service;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Service;

@Service
public class StockLoadStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    private final OrderService orderService;

    public StockLoadStreamListener(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            OrderService orderService) {
        this.container = container;
        this.orderService = orderService;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        orderService.retryPendingOrders(message.getValue());
    }

    @PostConstruct
    private void startListeningToMessages() {
        String streamKey = "stock:load";
        Subscription subscription = container.receive(
                StreamOffset.fromStart(streamKey),
                this
        );
        container.start();
    }
}
