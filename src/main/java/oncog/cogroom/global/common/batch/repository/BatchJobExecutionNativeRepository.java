package oncog.cogroom.global.common.batch.repository;

public interface BatchJobExecutionNativeRepository {
    /**
     * 오늘 자정 이후에 COMPLETED 상태로 성공한 Job 실행 횟수를 반환
     */
    int countTodayCompletedExecutions(String jobName);
}
