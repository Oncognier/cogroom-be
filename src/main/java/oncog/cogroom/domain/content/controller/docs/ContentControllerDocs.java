package oncog.cogroom.domain.content.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.content.dto.ContentResponseDTO;
import oncog.cogroom.domain.content.exception.ContentErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Content", description = "Content 관련 API")
public interface ContentControllerDocs {

    @ApiErrorCodeExamples(
            value = {ContentErrorCode.class, ApiErrorCode.class},
            include = {"IMAGE_NOT_FOUND", "CONTENTS_NOT_FOUND"})
    @Operation(summary = "콘텐츠 목록 조회(홈)", description = "홈 화면에서의 콘텐츠 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<ContentResponseDTO>>> getHomeContents();
}
