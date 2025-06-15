package oncog.cogroom.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.member.dto.MemberRequestDTO;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.domain.streak.service.StreakService;
import oncog.cogroom.global.common.service.BaseService;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static oncog.cogroom.domain.member.dto.MemberResponseDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService extends BaseService {

    private final MemberRepository memberRepository;
    private final StreakService streakService;
    private final EmailService emailService;

    // 테스트용 다음 이슈에서 삭제 예정
    private final JwtProvider jwtProvider;

    public MemberInfoDTO findMemberInfo() {
        Member member = getMember();

        return MemberInfoDTO.builder()
                .email(member.getEmail())
                .description(member.getDescription())
                .imageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    public MemberSummaryDTO findMemberSummary(HttpServletRequest request) {
        Member member = getMember();

        // 테스트용 다음 이슈에서 삭제 예정
        String accessToken = jwtProvider.resolveToken(request);
        log.info("now accessToken = {}", accessToken);

        return MemberSummaryDTO.builder()
                .imageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname())
                .build();
    }

    public MemberMyPageInfoDTO findMemberForMyPage() {
        Member member = getMember();

        // 가입 일과 오늘 날짜 사이의 일 수 차이 계산
        long days = ChronoUnit.DAYS.between(member.getCreatedAt().toLocalDate(), LocalDate.now()) + 1;

        // 스트릭 누적 일 수 계산
        int streakDays = streakService.getStreakDays(member);

        return MemberMyPageInfoDTO.builder()
                .nickname(member.getNickname())
                .signupDays(days)
                .streakDays(streakDays)
                .build();
    }


    public void updateMemberInfo(MemberRequestDTO.MemberInfoUpdateDTO request){
        Member member = getMember();

        // 닉네임 숫자로만 구성되어있는지 검사
        if(request.getNickname().matches("^\\d+$")) throw new MemberException(MemberErrorCode.NICKNAME_INVALID_PATTERN);

        emailService.isVerified(request.getEmail());

        member.updateMemberInfo(request);
    }

    public boolean existNickname(MemberRequestDTO.ExistNicknameDTO request) {

        if(Boolean.TRUE.equals(memberRepository.existsByNickname(request.getNickname()))){
            throw new MemberException(MemberErrorCode.DUPLICATE_USER_NICKNAME);
        }

        return false;
    }
}
