package oncog.cogroom.domain.admin.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.admin.service.AdminAuthService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminScheduler {
    private final AdminAuthService adminAuthService;

    @Scheduled(cron = "0 0 0 * * *")
    public void withDrawPendingMember() {

    }
}
