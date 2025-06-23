package oncog.cogroom.domain.daily.repository;

import oncog.cogroom.domain.daily.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {
}
