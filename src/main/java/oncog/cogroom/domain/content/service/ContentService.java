package oncog.cogroom.domain.content.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.content.dto.ContentResponseDTO;
import oncog.cogroom.domain.content.entity.Content;
import oncog.cogroom.domain.content.entity.ContentImage;
import oncog.cogroom.domain.content.enums.ContentStatus;
import oncog.cogroom.domain.content.exception.ContentErrorCode;
import oncog.cogroom.domain.content.exception.ContentException;
import oncog.cogroom.domain.content.repository.ContentImageRepository;
import oncog.cogroom.domain.content.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContentService {

    private static final int THUMBNAIL_IMAGE_DISPLAY_ORDER = 1;

    private final ContentRepository contentRepository;
    private final ContentImageRepository contentImageRepository;

    public List<ContentResponseDTO> getHomeContents() {
        List<Content> contents = contentRepository.findByStatus(ContentStatus.ONSALE);

        return contents.stream()
                .map(content -> ContentResponseDTO.builder()
                        .id(content.getId())
                        .title(content.getName())
                        .imageUrl(getContentImage(content).getImageUrl())
                        .summary(content.getSummary())
                        .build())
                .collect(Collectors.toList());
    }

    // 썸네일 이미지만 조회 (display_order = 1)
    private ContentImage getContentImage(Content content) {
        return contentImageRepository.findByContentAndDisplayOrderIs(content, THUMBNAIL_IMAGE_DISPLAY_ORDER)
                .orElseThrow(() -> new ContentException(ContentErrorCode.IMAGE_NOT_FOUND));
    }

}
