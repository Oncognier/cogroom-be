package oncog.cogroom.domain.daily.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.admin.dto.response.AdminResponse;
import oncog.cogroom.domain.category.entity.QCategory;
import oncog.cogroom.domain.daily.dto.response.DailyResponse;
import oncog.cogroom.domain.daily.entity.QAnswer;
import oncog.cogroom.domain.daily.entity.QAssignedQuestion;
import oncog.cogroom.domain.daily.entity.QQuestion;
import oncog.cogroom.domain.daily.entity.QQuestionCategory;
import oncog.cogroom.domain.daily.enums.QuestionLevel;
import oncog.cogroom.domain.daily.repository.AssignedQuestionQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssignedQuestionQueryRepositoryImpl implements AssignedQuestionQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DailyResponse.QuestionAnsweredKey> findPagedData(Long memberId,
                                                                 Pageable pageable,
                                                                 String category,
                                                                 String keyword,
                                                                 QuestionLevel questionLevel,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        QAssignedQuestion aq = QAssignedQuestion.assignedQuestion;
        QAnswer a = QAnswer.answer1;
        QQuestion q = QQuestion.question1;
        QQuestionCategory qc = QQuestionCategory.questionCategory;
        QCategory c = QCategory.category;

        // 공통 WHERE 조건: 회원 ID 일치 + 답변 완료된 질문
        BooleanBuilder conditions = new BooleanBuilder()
                .and(aq.member.id.eq(memberId))
                .and(aq.isAnswered.isTrue());


        if (keyword != null && !keyword.isBlank()) { // 키워드 필터
            conditions.and(q.question.contains(keyword));
        }
        if (category != null && !category.isBlank()) { // 카테고리 필터
            conditions.and(c.name.eq(category));
        }
        if (questionLevel != null) { // 난이도 필터
            conditions.and(q.level.eq(questionLevel));
        }
        if (startDate != null && endDate != null) { // 날짜 필터
            conditions.and(a.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)));
        }

        // 실제 조회 쿼리 생성 (질문 ID + 답변일시를 Key로 반환)
        JPQLQuery<DailyResponse.QuestionAnsweredKey> baseQuery = queryFactory
                .select(Projections.constructor(
                        DailyResponse.QuestionAnsweredKey.class,
                        q.id,
                        a.createdAt
                ))
                .from(aq)
                .join(aq.question, q)
                .join(a).on(a.member.eq(aq.member).and(a.question.eq(aq.question)))
                .join(qc).on(qc.question.id.eq(q.id))
                .join(qc.category, c)
                .where(conditions)
                .groupBy(q.id, a.createdAt)
                .orderBy(a.createdAt.desc());

        long total = baseQuery.fetch().size();

        // 페이지네이션 적용 후 결과 조회
        List<DailyResponse.QuestionAnsweredKey> content = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<AdminResponse.MemberDailyDTO> findMemberDailyDtoList(Long memberId,
                                                                     List<DailyResponse.QuestionAnsweredKey> keys,
                                                                     String category,
                                                                     String keyword,
                                                                     QuestionLevel questionLevel,
                                                                     LocalDate startDate,
                                                                     LocalDate endDate
                                                                     ) {

        // 키가 비어있으면 빈 리스트 반환
        if(keys.isEmpty()) return Collections.emptyList();

        QAssignedQuestion aq = QAssignedQuestion.assignedQuestion;
        QAnswer a = QAnswer.answer1;
        QQuestion q = QQuestion.question1;
        QQuestionCategory qc = QQuestionCategory.questionCategory;
        QCategory c = QCategory.category;

        // 키 조건 구성: (질문 ID + 답변일시)가 일치하는 레코드만
        BooleanBuilder keyConditions = new BooleanBuilder();
        for (DailyResponse.QuestionAnsweredKey key : keys) {
            keyConditions.or(q.id.eq(key.getQuestionId()).and(a.createdAt.eq(key.getCreatedAt())));
        }

        // 실제 데이터 조회: 각 질문에 대한 카테고리 포함 정보까지 조회
        return queryFactory
                .select(Projections.constructor(
                        AdminResponse.MemberDailyDTO.class,
                        q.question.as("questionText"),
                        q.level.as("questionLevel"),
                        c.name.as("category"), // // 단일 행 기준 카테고리
                        a.createdAt.as("answeredAt")
                ))
                .from(aq)
                .join(aq.question ,q)
                .join(a).on(a.member.eq(aq.member).and(a.question.eq(aq.question)))
                .join(qc).on(qc.question.id.eq(q.id))
                .join(qc.category, c)
                .where(aq.member.id.eq(memberId)
                        .and(keyConditions)) // 지정된 질문-답변일시에 해당하는 데이터만 조회
                .orderBy(a.createdAt.desc())
                .fetch();
    }

}
