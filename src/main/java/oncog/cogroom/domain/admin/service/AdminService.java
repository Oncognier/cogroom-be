package oncog.cogroom.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.dto.response.PageResponse;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.repository.QuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    // QueryDsl로 개선 고려
    public PageResponse<AdminResponse.MemberListDTO> findMemberList(Pageable pageable, LocalDate startDate, LocalDate endDate, String keyword) {

        // 필터의 시작일 또는 종료일에 대한 null 체크
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        // 사용자 조회
        Page<Member> pages = memberRepository.findMembersByFilter(keyword, startDateTime, endDateTime, pageable);

        // 사용자 정보 리스트 DTO로 변경
        List<AdminResponse.MemberListDTO> memberList = AdminResponse.MemberListDTO.of(pages.getContent());

        // 페이징 응답 데이터로 변경
        PageResponse<AdminResponse.MemberListDTO> memberListDTOPageResponse = PageResponse.of(pages, memberList);

        return memberListDTOPageResponse;

    }

    @Transactional
    public void createDailyQuestions(AdminRequest.DailyQuestionsDTO request) {
        List<AdminRequest.DailyQuestionsDTO.QuestionDTO> questionList = request.getQuestionList();

        List<Question> questions = questionList.stream()
                .map(question -> Question.builder()
                        .level(request.getLevel())
                        .question(question.getQuestion())
                        .build())
                .toList();

        questionRepository.saveAll(questions);
    }
}
