package oncog.cogroom.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.category.dto.response.CategoryResponse;
import oncog.cogroom.domain.category.entity.Category;
import oncog.cogroom.domain.category.repository.CategoryRepository;
import oncog.cogroom.global.common.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService extends BaseService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse.CategoryDTO> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> CategoryResponse.CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
