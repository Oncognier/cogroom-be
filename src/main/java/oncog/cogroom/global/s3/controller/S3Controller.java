package oncog.cogroom.global.s3.controller;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.global.common.response.ApiResponse;
import oncog.cogroom.global.s3.dto.S3RequestDTO;
import oncog.cogroom.global.s3.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/preSigned-url/upload")
    public ResponseEntity<ApiResponse<String>> getPreSignedUrl(@RequestBody S3RequestDTO.PreSignedUrlRequestDTO request) {
        String preSignedUrl = s3Service.generatePreSignedUrl(request.getFileName(), request.getContentType()); // 추후 DTO로 대체

        return ResponseEntity.ok(ApiResponse.success(preSignedUrl));
    }


}
