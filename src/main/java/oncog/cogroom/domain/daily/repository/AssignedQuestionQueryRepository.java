package oncog.cogroom.domain.daily.repository;

import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.daily.dto.response.DailyResponse;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AssignedQuestionQueryRepository {
    Page<DailyResponse.QuestionAnsweredKey> findPagedData(Long memberId,
                                                          Pageable pageable,
                                                          List<Integer> category,
                                                          String keyword,
                                                          List<QuestionLevel> questionLevel,
                                                          LocalDate startDate,
                                                          LocalDate endDate);

    List<AdminResponse.MemberDailyDTO> findMemberDailyDtoList(Long memberId,
                                                              List<DailyResponse.QuestionAnsweredKey> keys);

}
