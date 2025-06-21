package oncog.cogroom.global.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.exception.MemberException;
import oncog.cogroom.global.common.response.code.ApiErrorCode;
import oncog.cogroom.global.s3.dto.S3RequestDTO;
import oncog.cogroom.global.s3.dto.S3ResponseDTO;
import oncog.cogroom.global.s3.enums.UploadType;
import oncog.cogroom.global.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final JwtProvider jwtProvider;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.cloud-front}")
    private String cloudFrontUrl;


    // PreSignedUrl 생성
    public List<S3ResponseDTO.PreSignedResponseDTO> generatePreSignedUrl(S3RequestDTO.PreSignedUrlRequestDTO request) {

        return request.getFileSet()
                .entrySet()
                .stream()
                .map(file -> {

                    // 업로드 파일 요청 생성
                    PutObjectRequest uploadFile = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(generateKey(file.getKey()))
                            .contentType(file.getValue())
                            .build();

                    // preSignedUrl 요청 생성
                    PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(10))
                            .putObjectRequest(uploadFile)
                            .build();

                    String preSignedUrl = s3Presigner.presignPutObject(preSignRequest).url().toString();
                    String accessUrl = generateAccessUrl(file.getKey());

                    return S3ResponseDTO.PreSignedResponseDTO.builder()
                            .preSignedUrl(preSignedUrl)
                            .accessUrl(accessUrl)
                            .build();

                }).collect(Collectors.toList());
    }

    // 백엔드에서 직접 파일 업로드
    public String uploadFile(MultipartFile file, String directory) {
        String fileName = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("s3 파일 업로드 실패", e);
        }
    }

    // s3 파일 삭제 (추후 강의 기능 적용시 변경 사항 발생 가능)
    public void deleteFile(S3RequestDTO.DeleteFilesDTO request) {
        request.getFileUrls().forEach(fileUrl -> {
            String fileKey = extractKey(fileUrl);

            log.info("fileKey = {}", fileKey);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
        });
    }

    // TEMP -> 영구 디렉토리로 파일 복사
    public void copyFile(List<String> imageUrlList, UploadType uploadType) {
        imageUrlList.forEach(url -> {
            String tempUrl = extractKey(url);
            String finalUrl = tempUrl.replace("TEMP", uploadType.name());

            // temp -> 영구 uploadType 폴더로 복사
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(tempUrl)
                    .destinationBucket(bucket)
                    .destinationKey(finalUrl)
                    .build());

            // temp 디렉토리 내부에 저장된 데이터 삭제
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(tempUrl)
                    .build());
        });
    }

    public String extractKey(String fileUrl) {
        return fileUrl.replace(cloudFrontUrl, "");
    }

    // 저장될 파일 이름(파일 키) 생성
    private String generateKey(String fileName) {
        Long memberId = jwtProvider.extractMemberId();

        return String.format("%s/%d_%s", UploadType.TEMP.name(), memberId, fileName);
    }

    // CloudFront 접근 URL 생성
    private String generateAccessUrl(String fileName) {
        Long memberId = jwtProvider.extractMemberId();

        return String.format("%s%s/%d_%s", cloudFrontUrl, UploadType.TEMP, memberId, fileName);
    }
}



