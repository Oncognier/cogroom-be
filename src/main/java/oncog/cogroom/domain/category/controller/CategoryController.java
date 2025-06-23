package oncog.cogroom.domain.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.category.controller.docs.CategoryControllerDocs;
import oncog.cogroom.domain.category.dto.response.CategoryResponse;
import oncog.cogroom.domain.category.service.CategoryService;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiSuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponse.CategoryDTO>>> getCategories() {
        List<CategoryResponse.CategoryDTO> categoryList = categoryService.getCategories();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, categoryList));
    }


}
