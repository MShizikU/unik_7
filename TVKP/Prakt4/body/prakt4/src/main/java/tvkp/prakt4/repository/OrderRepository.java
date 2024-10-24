package tvkp.prakt4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tvkp.prakt4.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
