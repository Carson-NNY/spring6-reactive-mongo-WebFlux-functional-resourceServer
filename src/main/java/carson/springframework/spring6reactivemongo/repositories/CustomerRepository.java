package carson.springframework.spring6reactivemongo.repositories;

import carson.springframework.spring6reactivemongo.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
