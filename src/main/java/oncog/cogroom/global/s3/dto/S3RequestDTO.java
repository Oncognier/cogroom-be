package oncog.cogroom.global.s3.dto;

import lombok.Builder;
import lombok.Getter;

public class S3RequestDTO {

    @Builder
    @Getter
    public static class PreSignedUrlRequestDTO {
        private String fileName;
        private String contentType;
    }
}
