package oncog.cogroom.domain.content.repository;

import oncog.cogroom.domain.content.entity.Content;
import oncog.cogroom.domain.content.entity.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
    Optional<ContentImage> findByContentAndDisplayOrderIs(Content content, int displayOrder);
}
