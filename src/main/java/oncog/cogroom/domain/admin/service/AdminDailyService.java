package oncog.cogroom.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.admin.validator.AdminValidator;
import oncog.cogroom.domain.category.entity.Category;
import oncog.cogroom.domain.daily.dto.response.DailyResponse;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.entity.QuestionCategory;
import oncog.cogroom.domain.daily.entity.QuestionCategoryId;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.daily.repository.QuestionCategoryRepository;
import oncog.cogroom.domain.daily.repository.QuestionRepository;
import oncog.cogroom.global.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDailyService {

    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final AdminValidator adminValidator;

    // 조건 검색 -> QueryDsl로 개선 고려
    public PageResponse<AdminResponse.DailyQuestionsDTO> getDailyQuestions(Pageable pageable, List<Integer> categoryIds, List<String> levels, String keyword) {

        // 유효한 카테고리 검사
        if (categoryIds != null && !categoryIds.isEmpty()) {
            adminValidator.validateCategoriesByIds(categoryIds);
        }

        // 유효한 난이도 검사
        if (levels != null) {
            for (String level : levels) {
                adminValidator.validateQuestionLevel(level.toUpperCase());
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
        adminValidator.validatePageRange(page, pageable);

        return PageResponse.of(page, data);
    }

    @Transactional
    public void createDailyQuestions(AdminRequest.DailyQuestionsDTO request) {
        adminValidator.validateDailyQuestionRequest(request);

        List<AdminRequest.DailyQuestionsDTO.QuestionDTO> questionList = request.getQuestionList();
        List<Integer> requestedCategoryIds = request.getCategoryList();
        QuestionLevel level = QuestionLevel.valueOf(request.getLevel().toUpperCase());

        // 유효한 카테고리인지 확인
        List<Category> categories = adminValidator.validateCategoriesByIds(requestedCategoryIds);

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
                            .question(question)
                            .category(category)
                            .build())
                    .toList();

            questionCategoryRepository.saveAll(questionCategories);
        }
    }

    public PageResponse<AdminResponse.MemberDailyListDTO> getDailyContents(Long memberId, Pageable pageable,
                                                                           List<Integer> categories,
                                                                           String keyword,
                                                                           List<QuestionLevel> questionLevels,
                                                                           LocalDate startDate,
                                                                           LocalDate endDate
    ) {
        // 카테고리 유효성 검사
        adminValidator.validateCategoriesByIds(categories);

        // 질문 난이도 유효성 검사
        adminValidator.validateLevels(questionLevels);

        // 질문 내용과 답변 시간 조합으로 페이징 데이터 조회
        Page<DailyResponse.QuestionAnsweredKey> pagedData = assignedQuestionRepository.findPagedData(memberId, pageable,categories, keyword ,questionLevels,startDate,endDate);

        // 페이징 유효성 검사
        adminValidator.validatePageRange(pagedData, pageable);

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
                            .imageUrl(dto.getImageUrl())
                            .level(dto.getLevel())
                            .answeredAt(dto.getAnsweredAt())
                            .categories(new HashSet<>()).build()
            ).getCategories().add(dto.getCategory());
        }

        return grouped.values().stream().toList();
    }

}
