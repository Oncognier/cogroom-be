package oncog.cogroom.domain.member.repository;

import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String userEmail);

    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    Boolean existsByNickname(String nickname);
}
