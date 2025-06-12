package oncog.cogroom.domain.content.repository;

import oncog.cogroom.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
