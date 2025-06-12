package oncog.cogroom.domain.content.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.content.controller.docs.ContentControllerDocs;
import oncog.cogroom.domain.content.dto.ContentResponseDTO;
import oncog.cogroom.domain.content.service.ContentService;
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
@RequestMapping("/api/v1/contents")
public class ContentController implements ContentControllerDocs {

    private final ContentService contentService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<List<ContentResponseDTO>>> getHomeContents() {
        List<ContentResponseDTO> contentList = contentService.getHomeContents();

        return ResponseEntity.ok(ApiResponse.of(ApiSuccessCode.SUCCESS, contentList));
    }
}
