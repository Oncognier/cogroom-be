package oncog.cogroom.domain.auth.repository;

import oncog.cogroom.domain.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndVerifyCode(String email, String verificationCode);

    Optional<EmailVerification> findByEmail(String email);

    Boolean existsByEmailAndVerifyStatus(String email, boolean status);


}
