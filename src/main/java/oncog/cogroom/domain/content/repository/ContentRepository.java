package oncog.cogroom.domain.content.repository;

import oncog.cogroom.domain.content.entity.Content;
import oncog.cogroom.domain.content.enums.ContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByStatus(ContentStatus status);
}
