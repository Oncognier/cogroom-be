package oncog.cogroom.domain.member.service;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean isExist(String providerId) {
        return memberRepository.existsByProviderId(providerId);
    }

    public Member find(OAuth2User user) {
        return memberRepository.findByProviderId(user.getAttribute("providerId"))
                .orElseGet(() -> {
                    return Member.builder()
                            .email(user.getAttribute("email"))
                            .nickname(user.getAttribute("nickname"))
                            .role(MemberRole.USER)
                            .provider(user.getAttribute("provider"))
                            .providerId(user.getAttribute("providerId"))
                            .status(MemberStatus.ACTIVE)
                            .build();
                });
    }
}
