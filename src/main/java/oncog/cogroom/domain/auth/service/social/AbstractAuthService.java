package oncog.cogroom.domain.auth.service.social;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.request.AuthRequest;
import oncog.cogroom.domain.auth.dto.response.AuthResponse;
import oncog.cogroom.domain.auth.entity.WithdrawReason;
import oncog.cogroom.domain.auth.repository.WithdrawReasonRepository;
import oncog.cogroom.domain.auth.service.AuthService;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.auth.service.TokenService;
import oncog.cogroom.domain.auth.userInfo.SocialUserInfo;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Slf4j
public abstract class AbstractAuthService implements AuthService {
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final TokenUtil tokenUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    // 소셜 로그인 공통 로직
    public final AuthResponse.LoginResultDTO login(AuthRequest.LoginDTO request){
        String accessToken = requestAccessToken(request.getCode());
        SocialUserInfo userInfo = requestUserInfo(accessToken);
        // provider와 ProviderId를 복합 유니크 키로 검사하여 사용자 조회
        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId(request.getProvider(), userInfo.getProviderId());

        // 사용자 유무에 따른 로직 분기
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            // 탈퇴 유예 기간에 재로그인 하는 경우 다시 계정 활성화
            if(member.getStatus().equals(MemberStatus.PENDING)) member.updateMemberStatusToActive();

            AuthResponse.ServiceTokenDTO tokenDTO = tokenUtil.createTokens(member);

            // redis에 refreshToken 저장
            saveRefreshTokenToRedis(tokenDTO.getRefreshToken(), member.getId());

            return AuthResponse.LoginResultDTO.builder()
                    .socialUserInfo(null)
                    .tokens(tokenDTO)
                    .needSignup(false)
                    .build();
        }else{
            return AuthResponse.LoginResultDTO.builder()
                    .socialUserInfo(userInfo)
                    .tokens(null)
                    .needSignup(true)
                    .build();
        }
    }
    // 소셜 로그인의 경우 회원가입 클릭 -> 로그인 처리가 ui적으로 깔끔하기에 토큰 발급
    public final AuthResponse.SignupResultDTO signup(AuthRequest.SignupDTO request) {
        // 이메일 인증 유무 검사
        emailService.isVerified(request.getEmail());
        Member savedMember = memberRepository.save(Member.builder()
                .email(request.getEmail())
                .nickname(generateNickname())
                .role(MemberRole.USER)
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                .description(null)
                .phoneNumber(null)
                .profileImageUrl(null)
                .status(MemberStatus.ACTIVE)
                .build());

        AuthResponse.ServiceTokenDTO tokenDTO = tokenUtil.createTokens(savedMember);

        saveRefreshTokenToRedis(tokenDTO.getRefreshToken(), savedMember.getId());

        return AuthResponse.SignupResultDTO.builder()
                .tokens(tokenDTO)
                .build();
    }



    public final void saveRefreshTokenToRedis(String refreshToken, Long memberId) {
        redisTemplate.opsForValue().set("RT:" + memberId, refreshToken, refreshExpiration, TimeUnit.DAYS);
    }

    // 랜덤 닉네임 생성
    public final String generateNickname() {
        // 10 ~ 9999 까지 랜덤 수 할당
        int randomUUID = ThreadLocalRandom.current().nextInt(10, 10000);
        String randomName = String.format("%s%s","코그니어", randomUUID);

        if(Boolean.TRUE.equals(memberRepository.existsByNickname(randomName))) generateNickname();

        return randomName;
    }

    // 각 구현체에서 오버라이드된 메소드들이 실행됨
    protected abstract String requestAccessToken(String code);

    protected abstract SocialUserInfo requestUserInfo(String accessToken);

}