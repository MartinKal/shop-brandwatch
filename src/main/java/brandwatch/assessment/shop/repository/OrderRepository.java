package brandwatch.assessment.shop.repository;

import brandwatch.assessment.shop.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Set;

public interface OrderRepository extends MongoRepository<Order, String> {

    @Query("{'status': 'pending'}")
    Set<Order> findAllPending();
    @Query("{'status': 'pending', 'items.productId': ?0}")
    Set<Order> findAllPendingForProductId(String productId);
}
