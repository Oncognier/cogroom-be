package oncog.cogroom.domain.auth.repository;

import oncog.cogroom.domain.auth.entity.WithdrawReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawReasonRepository extends JpaRepository<WithdrawReason, Long> {


}
