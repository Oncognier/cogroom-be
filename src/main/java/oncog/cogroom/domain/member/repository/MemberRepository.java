package oncog.cogroom.domain.member.repository;

import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String userEmail);

    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    Boolean existsByNickname(String nickname);

    Page<Member> findAll(Pageable pageable);


    @Query("""
    SELECT m FROM Member m
    WHERE(:keyword IS NULL OR m.nickname LIKE %:keyword% OR m.email LIKE %:keyword%)
    AND(:startDate IS NULL OR m.createdAt >= :startDate)
    AND(:endDate IS NULL OR m.createdAt <= :endDate)
    """)

    Page<Member> findMembersByFilter(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
