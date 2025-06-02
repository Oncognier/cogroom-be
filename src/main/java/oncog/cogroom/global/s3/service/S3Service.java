package oncog.cogroom.global.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.cloud-front}")
    private String cloudFrontUrl;

    // 단일 파일 업로드
    public String generatePreSignedUrl(String fileName, String contentType) {
        PutObjectRequest uploadFile = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(uploadFile)
                .build();

        PresignedPutObjectRequest preSignedRequest = s3Presigner.presignPutObject(preSignRequest);

        return preSignedRequest.url().toString();
    }

    // 복수 파일 업로드
    public List<String> generatePreSignedUrl(Map<String, String> fileSet) {

        return fileSet.entrySet().stream().map(file -> {
            // 업로드 파일 요청 생성
            PutObjectRequest uploadFile = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(file.getKey())
                    .contentType(file.getValue())
                    .build();

            // preSignedUrl 요청 생성
            PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(uploadFile)
                    .build();

            return s3Presigner.presignPutObject(preSignRequest).url().toString();
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
            throw new RuntimeException("s3 파일 업로드 실패",e);
        }
    }

    // s3 파일 삭제
    public void deleteFile(String fileUrl) {
        String fileKey = extractKey(fileUrl);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
    public String extractKey(String fileUrl) {
        return fileUrl.replace(cloudFrontUrl, "");
    }


}
