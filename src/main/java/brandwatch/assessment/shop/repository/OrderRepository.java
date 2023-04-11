package brandwatch.assessment.shop.repository;

import brandwatch.assessment.shop.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
