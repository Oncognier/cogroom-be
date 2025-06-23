package oncog.cogroom.domain.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class AdminRequest {

    @Getter
    @AllArgsConstructor
    public static class DailyQuestionsDTO {
        private List<Integer> categoryList;
        private String level;
        private List<QuestionDTO> questionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionDTO {
            private String question;
        }

    }

}
