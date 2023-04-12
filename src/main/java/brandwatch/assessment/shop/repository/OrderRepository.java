package brandwatch.assessment.shop.repository;

import brandwatch.assessment.shop.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    @Query("{'status': 'pending', 'items.productId': ?0}")
    List<Order> findAllPendingForProductId(String productId);
}
