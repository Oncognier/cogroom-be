package oncog.cogroom.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.global.common.response.PageResponse;
import oncog.cogroom.domain.admin.exception.AdminErrorCode;
import oncog.cogroom.domain.admin.exception.AdminException;
import oncog.cogroom.domain.category.entity.Category;
import oncog.cogroom.domain.category.repository.CategoryRepository;
import oncog.cogroom.domain.daily.dto.response.DailyResponse;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.entity.QuestionCategory;
import oncog.cogroom.domain.daily.entity.QuestionCategoryId;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.daily.repository.QuestionCategoryRepository;
import oncog.cogroom.domain.daily.repository.QuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberRole;
import oncog.cogroom.domain.member.exception.MemberErrorCode;
import oncog.cogroom.domain.member.exception.MemberException;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService extends BaseService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;

    // QueryDsl로 개선 고려
    public PageResponse<AdminResponse.MemberListDTO> findMemberList(Pageable pageable, LocalDate startDate, LocalDate endDate, String keyword) {

        // 필터의 시작일 또는 종료일에 대한 null 체크
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        // 사용자 조회
        Page<Member> pages = memberRepository.findMembersByFilter(keyword, startDateTime, endDateTime, pageable);

        // 페이징 유효성 검사
        validatePageRange(pages, pageable);

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
            validateCategoriesByIds(categoryIds);
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
        List<Category> categories = this.validateCategoriesByIds(requestedCategoryIds);

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
    private List<Category> validateCategoriesByIds(List<Integer> categoryIds) {
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
            throw new AdminException(AdminErrorCode.CATEGORY_INVALID_ERROR);
        }

        return categories;
    }

    private void validateCategoriesByNames(List<String> categories) {

        // DB에 존재하는 카테고리 이름 리스트 조회
        List<String> categoryNames = categoryRepository.findAllName();

        // 요청 카테고리 Name과 실제 카테고리 비교
        List<String> invalidCategoryNames = categories.stream()
                .filter(categoryNames::contains)
                .toList();

        if (!invalidCategoryNames.isEmpty()) {
            throw new AdminException(AdminErrorCode.CATEGORY_INVALID_ERROR);
        }
    }

    private void validateLevels(List<QuestionLevel> questionLevels) {
        List<QuestionLevel> questionLevelList = Arrays.stream(QuestionLevel.values()).toList();

        questionLevels.stream()
                .forEach(level -> {
                    if(!questionLevelList.contains(level)) throw new AdminException(AdminErrorCode.LEVEL_INVALID_ERROR);
                });
    }

    // 유효한 난이도인지 확인
    private void validateQuestionLevel(String level) {
        try {
            QuestionLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AdminException(AdminErrorCode.LEVEL_INVALID_ERROR);
        }
    }

    // 유효한 페이지인지 확인 (범위 초과 여부)
    private void validatePageRange(Page<?> page, Pageable pageable) {
        if (page.getTotalPages() > 0 && pageable.getPageNumber() > page.getTotalPages() - 1) {
            throw new AdminException(AdminErrorCode.PAGE_OUT_OF_RANGE_ERROR);
        }
    }

    // 사용자 삭제 (status 변경)
    public void deleteMembers(AdminRequest.DeleteMembersDTO request) {
        List<Long> memberIdList = request.getMemberIdList();

        memberIdList.stream().forEach(id -> memberRepository.findById(id).ifPresent(Member::withDrawMember));
    }

    // 사용자 권한 변경
    public void updateMemberRole(Long memberId, MemberRole role) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        member.updateMemberRole(role);
    }

    public PageResponse<AdminResponse.MemberDailyListDTO> getDailyContents(Long memberId, Pageable pageable,
                                                                           List<String> categories,
                                                                           String keyword,
                                                                           List<QuestionLevel> questionLevels,
                                                                           LocalDate startDate,
                                                                           LocalDate endDate
                                                                           ) {
        // 카테고리 유효성 검사
        validateCategoriesByNames(categories);

        // 질문 난이도 유효성 검사
        validateLevels(questionLevels);

            // 질문 내용과 답변 시간 조합으로 페이징 데이터 조회
        Page<DailyResponse.QuestionAnsweredKey> pagedData = assignedQuestionRepository.findPagedData(memberId, pageable,categories, keyword ,questionLevels,startDate,endDate);

        // 페이징 유효성 검사
        validatePageRange(pagedData, pageable);

        // (질문, 난이도, 카테고리, 답변 시간) 데이터 리스트 형태로 조회
        List<AdminResponse.MemberDailyDTO> memberDailyDtoList = assignedQuestionRepository.findMemberDailyDtoList(memberId, pagedData.getContent());

        // (질문, 난이도, 카테고리 리스트, 답변 시간) 형식으로 데이터 가공
        List<AdminResponse.MemberDailyListDTO> memberDailyListDtoList = groupByQuestion(memberDailyDtoList);

        return PageResponse.of(pagedData, memberDailyListDtoList);

    }

    // 각 질문의 카테고리를 단일 카테고리에서 리스트 형태로 변경
    private List<AdminResponse.MemberDailyListDTO> groupByQuestion(List<AdminResponse.MemberDailyDTO> flatList) {
        Map<String, AdminResponse.MemberDailyListDTO> grouped = new LinkedHashMap<>();

        for (AdminResponse.MemberDailyDTO dto : flatList) {
            String key = dto.getQuestion() + "::" + dto.getAnsweredAt();

            grouped.computeIfAbsent(key, k ->
                    AdminResponse.MemberDailyListDTO.builder()
                            .assignedQuestionId(dto.getAssignedQuestionId())
                            .nickname(dto.getNickname())
                            .question(dto.getQuestion())
                            .level(dto.getLevel())
                            .answeredAt(dto.getAnsweredAt())
                            .categories(new HashSet<>()).build()
            ).getCategories().add(dto.getCategory());
        }

        return grouped.values().stream().toList();
    }

}
