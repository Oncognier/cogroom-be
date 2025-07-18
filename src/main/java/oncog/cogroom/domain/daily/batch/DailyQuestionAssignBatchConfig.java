package oncog.cogroom.domain.daily.batch;

import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.repository.MemberRepository;
import oncog.cogroom.global.common.batch.logging.BatchJobLogger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailyQuestionAssignBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BatchJobLogger batchJobLogger;

    private final MemberRepository memberRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final DailyQuestionAssignProcessor dailyQuestionAssignProcessor;

    private static final int CHUNK_SIZE = 200; // 한 번에 처리할 데이터 개수

    @Bean
    public Job dailyQuestionAssignJob() {
        return new JobBuilder("dailyQuestionAssignJob", jobRepository)
                .incrementer(new RunIdIncrementer())    // 매번 새로운 JobInstance 생성
                .listener(batchJobLogger)
                .start(dailyQuestionAssignStep())
                .build();
    }

    @Bean
    public Step dailyQuestionAssignStep() {
        return new StepBuilder("dailyQuestionAssignStep", jobRepository)
                .<Member, AssignedQuestion>chunk(CHUNK_SIZE, transactionManager) // 200개씩 한번에 저장
                .reader(memberItemReader())
                .processor(dailyQuestionAssignProcessor)
                .writer(assignedQuestionItemWriter())
                .build();
    }

    // Member 데이터 페이징해서 읽기
    @Bean
    public RepositoryItemReader<Member> memberItemReader() {
        return new RepositoryItemReaderBuilder<Member>()
                .name("memberItemReader")
                .repository(memberRepository)
                .methodName("findByStatus")
                .arguments(MemberStatus.ACTIVE) // ACTIVE인 멤버만 읽어오기
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    // 할당된 질문 저장
    @Bean
    public RepositoryItemWriter<AssignedQuestion> assignedQuestionItemWriter() {
        return new RepositoryItemWriterBuilder<AssignedQuestion>()
                .repository(assignedQuestionRepository)
                .methodName("save")
                .build();
    }
}
