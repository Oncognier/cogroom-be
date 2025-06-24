package oncog.cogroom.domain.category.repository;

import oncog.cogroom.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("""
    SELECT name from Category 
    """)
    List<String> findAllName();
}
