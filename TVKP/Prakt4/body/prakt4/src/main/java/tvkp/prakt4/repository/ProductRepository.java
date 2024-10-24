package tvkp.prakt4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tvkp.prakt4.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
