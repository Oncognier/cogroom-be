package oncog.cogroom.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.security.domain.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

        log.info("사용자 조회 완료");

        return CustomUserDetails.builder()
                .memberId(member.getId())
                .memberEmail(member.getEmail())
                .role(member.getRole())
                .provider(member.getProvider())
                .build();
    }
}
