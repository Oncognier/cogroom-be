package oncog.cogroom.domain.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import oncog.cogroom.domain.daily.enums.QuestionLevel;

import java.util.List;

public class AdminRequest {

    @Getter
    @AllArgsConstructor
    public static class DailyQuestionsDTO {
        private Integer categoryId;
        private QuestionLevel level;
        private List<QuestionDTO> questionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionDTO {
            private String question;
        }

    }

}
