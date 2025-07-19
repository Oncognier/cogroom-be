package oncog.cogroom.global.common.batch.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BatchJobExecutionNativeRepositoryImpl implements BatchJobExecutionNativeRepository {

    private final EntityManager entityManager;

    @Override
    public int countTodayCompletedExecutions(String jobName) {
        String sql = """
            SELECT COUNT(*)
            FROM BATCH_JOB_EXECUTION e
            JOIN BATCH_JOB_INSTANCE i ON e.JOB_INSTANCE_ID = i.JOB_INSTANCE_ID
            WHERE i.JOB_NAME = ?
              AND e.CREATE_TIME >= CURDATE()
              AND e.STATUS = 'COMPLETED'
        """;

        Number result = (Number) entityManager.createNativeQuery(sql)
                .setParameter(1, jobName)
                .getSingleResult();

        return result.intValue();
    }
}

