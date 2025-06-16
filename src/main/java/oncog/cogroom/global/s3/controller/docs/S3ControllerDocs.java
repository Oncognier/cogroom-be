package oncog.cogroom.global.s3.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import oncog.cogroom.global.s3.dto.S3RequestDTO;
import oncog.cogroom.global.s3.dto.S3ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "S3", description = "S3 관련 API")
public interface S3ControllerDocs {
    @Operation(summary = "PreSignedUrl 요청", description = "PreSignedUrl과 accessUrl을 요청합니다.")
    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"INTERNAL_SERVER_ERROR", "TOKEN_INVALID_ERROR", "TOKEN_BLACK_LIST_ERROR", "TOKEN_EXPIRED_ERROR" ,"TOKEN_EMPTY_ERROR"})

    ResponseEntity<ApiResponse<List<S3ResponseDTO.PreSignedResponseDTO>>> getPreSignedUrl(@RequestBody S3RequestDTO.PreSignedUrlRequestDTO request);

    @Operation(summary = "S3 파일 삭제", description = "S3에 존재하는 파일 삭제를 요청합니다. (백엔드 테스트용)")
    @ApiErrorCodeExamples(
            value = {AuthErrorCode.class, ApiErrorCode.class},
            include = {"INTERNAL_SERVER_ERROR", "TOKEN_INVALID_ERROR", "TOKEN_BLACK_LIST_ERROR", "TOKEN_EXPIRED_ERROR" ,"TOKEN_EMPTY_ERROR"})


    ResponseEntity<ApiResponse<Void>> deleteFile(@RequestBody S3RequestDTO.DeleteFilesDTO request);

}
