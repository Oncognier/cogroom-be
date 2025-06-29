package oncog.cogroom.domain.member.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.member.service.MemberService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberScheduler {
    private final MemberService memberService;

    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(cron = "0 */1 * * * *")
    public void withDrawPendingMember() {
        log.info("탈퇴 후 30일 경과된 사용자 삭제 시작");
        memberService.deletePendingMember();
        log.info("탈퇴 후 30일 경과된 사용자 삭제 완료");
    }
}
