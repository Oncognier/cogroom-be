package oncog.cogroom.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.validator.AdminValidator;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.response.PageResponse;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminAuthService extends BaseService {

    private final MemberRepository memberRepository;
    private final AdminValidator adminValidator;

    // QueryDsl로 개선 고려
    public PageResponse<AdminResponse.MemberListDTO> findMemberList(Pageable pageable, LocalDate startDate, LocalDate endDate, String keyword) {

        // 필터의 시작일 또는 종료일에 대한 null 체크
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        // 사용자 조회
        Page<Member> pages = memberRepository.findMembersByFilter(keyword, startDateTime, endDateTime, pageable);

        // 페이징 유효성 검사
        adminValidator.validatePageRange(pages, pageable);

        // 사용자 정보 리스트 DTO로 변경
        List<AdminResponse.MemberListDTO> memberList = AdminResponse.MemberListDTO.of(pages.getContent());

        // 페이징 응답 데이터로 변경
        PageResponse<AdminResponse.MemberListDTO> memberListDTOPageResponse = PageResponse.of(pages, memberList);

        return memberListDTOPageResponse;

    }


    // 사용자 삭제 (status 변경)
    public void deleteMembers(AdminRequest.DeleteMembersDTO request) {
        List<Long> memberIdList = request.getMemberIdList();

        memberIdList.stream().forEach(id -> memberRepository.findById(id).ifPresent(Member::updateMemberStatusToPending));
    }

    // 사용자 권한 변경
    public void updateMemberRole(Long memberId, MemberRole role) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        member.updateMemberRole(role);
    }

    public void deletePendingMember() {
        List<Member> pendingMembers = memberRepository.findByStatus(MemberStatus.PENDING);

        if (pendingMembers.isEmpty()) {
            return;
        }

        List<Member> withDrawMembers = pendingMembers.stream()
                .filter(member -> LocalDate.now().isAfter(member.getUpdatedAt().toLocalDate().plusDays(30)))
                .collect(Collectors.toList());

        memberRepository.deleteAll(withDrawMembers);
        }
    }


