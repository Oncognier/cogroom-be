package oncog.cogroom.global.s3.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public class S3RequestDTO {

    @Builder
    @Getter
    public static class PreSignedUrlRequestDTO {
        private Map<String, String> fileSet;
    }
}
