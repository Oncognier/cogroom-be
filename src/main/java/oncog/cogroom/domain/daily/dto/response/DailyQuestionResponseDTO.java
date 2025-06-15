package oncog.cogroom.domain.daily.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyQuestionResponseDTO {
    private Long questionId;
    private Long assignedQuestionId;
    private String question;
    private String answer; // 질문 답변한 경우에만 반환


    @Getter
    @Builder
    public static class AssignedQuestionWithAnswerDTO{
        private String question;
        private String answer;
        private LocalDateTime assignedDate;
        private boolean isUpdatable;

        public void setUpdatable(boolean status) {
            this.isUpdatable = status;
        }

        public AssignedQuestionWithAnswerDTO(String question, String answer, LocalDateTime assignedDate) {
            this.question = question;
            this.answer = answer;
            this.assignedDate = assignedDate;
            this.isUpdatable = false;
        }
    }
}
