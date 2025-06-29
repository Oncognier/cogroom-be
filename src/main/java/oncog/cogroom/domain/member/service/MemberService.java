package oncog.cogroom.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.service.EmailService;
import oncog.cogroom.domain.member.dto.request.MemberRequest;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.event.MembersWithDrawnEvent;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.domain.streak.service.StreakService;
import oncog.cogroom.global.common.service.BaseService;
import oncog.cogroom.global.s3.enums.UploadType;
import oncog.cogroom.global.s3.service.S3Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static oncog.cogroom.domain.member.dto.response.MemberResponse.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService extends BaseService {

    private final MemberRepository memberRepository;
    private final StreakService streakService;
    private final EmailService emailService;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;

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

    public MemberSummaryDTO findMemberSummary() {
        Member member = getMember();

        return MemberSummaryDTO.builder()
                .imageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname())
                .memberRole(member.getRole())
                .build();
    }

    public MemberMyPageInfoDTO findMemberForMyPage() {
        Member member = getMember();

        // 가입 일과 오늘 날짜 사이의 일 수 차이 계산
        long days = ChronoUnit.DAYS.between(member.getCreatedAt().toLocalDate(), LocalDate.now()) + 1;

        // 스트릭 누적 일 수 계산
        int dailyStreak = streakService.getDailyStreak(member);

        return MemberMyPageInfoDTO.builder()
                .nickname(member.getNickname())
                .signupDays(days)
                .dailyStreak(dailyStreak)
                .build();
    }

    public void updateMemberInfo(MemberRequest.MemberInfoUpdateDTO request){
        Member member = getMember();

        emailService.isVerified(request.getEmail());

        // temp -> profile or content 디렉토리로 복사
        String finalUrlList = s3Service.copyFile(request.getImageUrl(), UploadType.PROFILE);

        // 변경된 이미지 경로 설정
        if (!finalUrlList.isEmpty()) {
            request.updateImageUrl(finalUrlList);
        }

        member.updateMemberInfo(request);
    }


    public boolean existNickname(MemberRequest.ExistNicknameDTO request) {

        if(Boolean.TRUE.equals(memberRepository.existsByNickname(request.getNickname()))){
            throw new MemberException(MemberErrorCode.NICKNAME_DUPLICATE_ERROR);
        }

        return false;
    }

    // 탈퇴한 사용자 데이터 생성 또는 조회 (기본적으로 1개 필요해서 생성)
    public Member getOrCreateUnknownMember() {
        return memberRepository.findByEmail("unknown@system.com")
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email("unknown@system.com")
                                .nickname("탈퇴한 사용자")
                                .status(MemberStatus.WITHDRAWN)
                                .build()
                ));
    }

    // 탈퇴 후 30일 지난 회원 삭제 및 관련 데이터 사용자 변경
    public void deletePendingMember() {
        List<Member> pendingMembers = memberRepository.findByStatus(MemberStatus.PENDING);

        if (!pendingMembers.isEmpty()) {
            List<Member> withdrawnMembers = pendingMembers.stream()
                    .filter(member -> LocalDate.now().isAfter(member.getUpdatedAt().toLocalDate().plusDays(30)))
                    .collect(Collectors.toList());

            eventPublisher.publishEvent(new MembersWithDrawnEvent(withdrawnMembers));

            memberRepository.deleteAll(withdrawnMembers);
        }
    }
}
