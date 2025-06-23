package oncog.cogroom.domain.category.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.category.dto.response.CategoryResponse;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Category", description = "카테고리 관련 API")
public interface CategoryControllerDocs {

    @Operation(summary = "카테고리 목록 조회", description = "카테고리 코드 및 이름을 조회합니다.")
    @ApiErrorCodeExamples(
            value = {ApiErrorCode.class},
            include = {"INTERNAL_SERVER_ERROR"})
    ResponseEntity<ApiResponse<List<CategoryResponse.CategoryDTO>>> getCategories();
}
