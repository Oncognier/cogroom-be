package oncog.cogroom.global.s3.dto;

import lombok.Builder;
import lombok.Getter;

public class S3ResponseDTO {

    @Getter
    @Builder
    public static class PreSignedResponseDTO {
        private String preSignedUrl;
        private String accessUrl;
    }
}
