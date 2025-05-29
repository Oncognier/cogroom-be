package oncog.cogroom.domain.auth.repository;

import oncog.cogroom.domain.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailVerification, Long> {

}
