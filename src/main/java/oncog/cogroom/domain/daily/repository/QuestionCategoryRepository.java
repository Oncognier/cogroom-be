package oncog.cogroom.domain.daily.repository;

import oncog.cogroom.domain.daily.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {
   List<QuestionCategory> findAllByIdQuestionId(Long id);
}
