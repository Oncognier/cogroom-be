package oncog.cogroom.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.dto.response.PageResponse;
import oncog.cogroom.domain.admin.exception.AdminErrorCode;
import oncog.cogroom.domain.admin.exception.AdminException;
import oncog.cogroom.domain.category.entity.Category;
import oncog.cogroom.domain.category.repository.CategoryRepository;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.entity.QuestionCategory;
import oncog.cogroom.domain.daily.entity.QuestionCategoryId;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.repository.QuestionCategoryRepository;
import oncog.cogroom.domain.daily.repository.QuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService extends BaseService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionCategoryRepository questionCategoryRepository;

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

    // 조건 검색 -> QueryDsl로 개선 고려
    public PageResponse<AdminResponse.DailyQuestionsDTO> getDailyQuestions(Pageable pageable, List<Integer> categoryIds, List<String> levels, String keyword) {

        // 유효한 카테고리 검사
        if (categoryIds != null && !categoryIds.isEmpty()) {
            validateCategories(categoryIds);
        }

        // 유효한 난이도 검사
        if (levels != null) {
            for (String level : levels) {
                validateQuestionLevel(level.toUpperCase());
            }
        }

        List<Integer> filteredCategoryIds = (categoryIds == null || categoryIds.isEmpty()) ? null : categoryIds;
        List<String> filteredLevels = (levels == null || levels.isEmpty()) ? null : levels;
        String filteredKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        // 조회 조건으로 필터링
        Page<Question> page = questionRepository.findDailyQuestionsByFilter(
                filteredCategoryIds, filteredLevels, filteredKeyword, pageable
        );

        // DTO 변환
        List<AdminResponse.DailyQuestionsDTO> data = page.stream()
                .map(question -> {
                    List<String> categoryNames = questionCategoryRepository.findAllByIdQuestionId(question.getId()).stream()
                            .map(qc -> qc.getCategory().getName())
                            .toList();
                    return AdminResponse.DailyQuestionsDTO.of(question, categoryNames);
                })
                .toList();

        // 유효한 페이지인지 확인
        validatePageRange(page, pageable);

        return PageResponse.of(page, data);
    }

    @Transactional
    public void createDailyQuestions(AdminRequest.DailyQuestionsDTO request) {
        validateDailyQuestionRequest(request);

        List<AdminRequest.DailyQuestionsDTO.QuestionDTO> questionList = request.getQuestionList();
        List<Integer> requestedCategoryIds = request.getCategoryList();
        QuestionLevel level = QuestionLevel.valueOf(request.getLevel().toUpperCase());

        // 유효한 카테고리인지 확인
        List<Category> categories = validateCategories(requestedCategoryIds);

        for (AdminRequest.DailyQuestionsDTO.QuestionDTO questionDTO : questionList) {
            Question question = questionRepository.save(
                    Question.builder()
                            .question(questionDTO.getQuestion())
                            .level(level)
                            .build()
            );

            List<QuestionCategory> questionCategories = categories.stream()
                    .map(category -> QuestionCategory.builder()
                            .id(new QuestionCategoryId(question.getId(), category.getId()))
                            .build())
                    .toList();

            questionCategoryRepository.saveAll(questionCategories);
        }
    }

    // 데일리 질문 등록 시 request 유효성 검사 (질문, 카테고리, 난이도 값 여부)
    private void validateDailyQuestionRequest(AdminRequest.DailyQuestionsDTO request) {
        if (CollectionUtils.isEmpty(request.getQuestionList())) {
            throw new AdminException(AdminErrorCode.QUESTION_LIST_EMPTY_ERROR);
        }
        if (CollectionUtils.isEmpty(request.getCategoryList())) {
            throw new AdminException(AdminErrorCode.CATEGORY_EMPTY_ERROR);
        }
        if (request.getLevel() == null) {
            throw new AdminException(AdminErrorCode.LEVEL_EMPTY_ERROR);
        }

        validateQuestionLevel(request.getLevel().toUpperCase());
    }

    // 카테고리 유효성 검사 (카테고리 존재 여부)
    private List<Category> validateCategories(List<Integer> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        // 실제 존재하는 카테고리 id set
        Set<Integer> existingIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        // 요청 카테고리 id와 실제 카테고리 비교
        List<Integer> invalidCategoryIds = categoryIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!invalidCategoryIds.isEmpty()) {
            throw new AdminException(AdminErrorCode.INVALID_CATEGORY_ERROR);
        }

        return categories;
    }

    // 유효한 난이도인지 확인
    private void validateQuestionLevel(String level) {
        try {
            QuestionLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AdminException(AdminErrorCode.INVALID_LEVEL_ERROR);
        }
    }

    // 유효한 페이지인지 확인 (범위 초과 여부)
    private void validatePageRange(Page<?> page, Pageable pageable) {
        if (page.getTotalPages() > 0 && pageable.getPageNumber() > page.getTotalPages() - 1) {
            throw new AdminException(AdminErrorCode.PAGE_OUT_OF_RANGE_ERROR);
        }
    }

}
