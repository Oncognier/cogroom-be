package oncog.cogroom.global.s3.dto;

import lombok.Builder;
import lombok.Getter;
import oncog.cogroom.global.s3.enums.UploadType;

import java.util.List;
import java.util.Map;

public class S3RequestDTO {

    @Builder
    @Getter
    public static class PreSignedUrlRequestDTO {
        private Map<String, String> fileSet;
    }


    @Builder
    @Getter
    public static class DeleteFilesDTO{
        private List<String> fileUrls;
    }

    @Builder
    @Getter
    public static class SaveFilesDTO {
        private List<String> saveFileUrlList;
    }
}
