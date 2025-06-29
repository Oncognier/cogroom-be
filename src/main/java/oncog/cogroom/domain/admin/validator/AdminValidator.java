package oncog.cogroom.domain.admin.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.dto.request.AdminRequest;
import oncog.cogroom.domain.admin.exception.AdminErrorCode;
import oncog.cogroom.domain.admin.exception.AdminException;
import oncog.cogroom.domain.category.entity.Category;
import oncog.cogroom.domain.category.repository.CategoryRepository;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminValidator {

    private final CategoryRepository categoryRepository;

    // 데일리 질문 등록 시 request 유효성 검사 (질문, 카테고리, 난이도 값 여부)
    public void validateDailyQuestionRequest(AdminRequest.DailyQuestionsDTO request) {
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
    public List<Category> validateCategoriesByIds(List<Integer> categoryIds) {
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

    public void validateLevels(List<QuestionLevel> questionLevels) {
        if(questionLevels == null) return;

        List<QuestionLevel> questionLevelList = Arrays.stream(QuestionLevel.values()).toList();

        questionLevels.stream()
                .forEach(level -> {
                    if(!questionLevelList.contains(level)) throw new AdminException(AdminErrorCode.LEVEL_INVALID_ERROR);
                });
    }

    // 유효한 난이도인지 확인
    public void validateQuestionLevel(String level) {
        try {
            QuestionLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AdminException(AdminErrorCode.LEVEL_INVALID_ERROR);
        }
    }

    // 유효한 페이지인지 확인 (범위 초과 여부)
    public void validatePageRange(Page<?> page, Pageable pageable) {
        if (page.getTotalPages() > 0 && pageable.getPageNumber() > page.getTotalPages() - 1) {
            throw new AdminException(ApiErrorCode.PAGE_OUT_OF_RANGE_ERROR);
        }
    }
}
