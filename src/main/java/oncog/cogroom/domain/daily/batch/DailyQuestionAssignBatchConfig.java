package oncog.cogroom.domain.daily.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.daily.entity.AssignedQuestion;
import oncog.cogroom.domain.daily.entity.Question;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.repository.AssignedQuestionRepository;
import oncog.cogroom.domain.daily.repository.QuestionRepository;
import oncog.cogroom.domain.daily.service.DailyQuestionAssignService;
import oncog.cogroom.domain.member.entity.Member;
import oncog.cogroom.domain.member.enums.MemberStatus;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyQuestionAssignBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobExecutionLogger jobExecutionLogger;

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final DailyQuestionAssignService dailyQuestionAssignService;

    private final Random random = new Random();
    private static final int CHUNK_SIZE = 200; // 한 번에 처리할 데이터 개수

    @Bean
    public Job dailyQuestionAssignJob() {
        return new JobBuilder("dailyQuestionAssignJob", jobRepository)
                .incrementer(new RunIdIncrementer())    // 매번 새로운 JobInstance 생성
                .listener(jobExecutionLogger)
                .start(dailyQuestionAssignStep())
                .build();
    }

    @Bean
    public Step dailyQuestionAssignStep() {
        return new StepBuilder("dailyQuestionAssignStep", jobRepository)
                .<Member, AssignedQuestion>chunk(CHUNK_SIZE, transactionManager) // 200개씩 한번에 저장
                .reader(memberItemReader())
                .processor(questionAssignProcessor())
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

    // 비즈니스 로직 처리
    @Bean
    public ItemProcessor<Member, AssignedQuestion> questionAssignProcessor() {
        return new ItemProcessor<Member, AssignedQuestion>() {
            @Override
            public AssignedQuestion process(Member member) throws Exception {
                // 중복 할당 방지 체크
                if (dailyQuestionAssignService.alreadyAssignedQuestionToday(member)) {
                    log.info("멤버: {} 에게 이미 질문이 할당되었습니다.", member.getId());
                    return null;
                }

                // 다음 질문 레벨 확인
                QuestionLevel level = dailyQuestionAssignService.getNextQuestionLevel(member);
                log.info("멤버 {} 에게 질문 레벨 {}을 할당합니다.", member.getId(), level);

                // 할당 가능한 질문 목록 조회
                List<Question> candidates = (level != null)
                        ? questionRepository.findUnansweredByMemberAndLevel(member.getId(), level)
                        : questionRepository.findAll();

                if (candidates.isEmpty()) {
                    log.info("할당 가능한 질문이 없습니다. memberId={}, level={}", member.getId(), level);
                    return null;
                }

                // 질문 랜덤으로 설정
                Question question = candidates.get(random.nextInt(candidates.size()));

                return AssignedQuestion.builder()
                        .member(member)
                        .question(question)
                        .isAnswered(false)
                        .assignedDate(LocalDateTime.now().toLocalDate().atStartOfDay())
                        .build();
            }
        };
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
