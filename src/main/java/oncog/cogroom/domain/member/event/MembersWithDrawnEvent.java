package oncog.cogroom.domain.member.event;

import oncog.cogroom.domain.member.entity.Member;

import java.util.List;

public class MembersWithDrawnEvent {

    private final List<Member> withdrawnMembers;

    public MembersWithDrawnEvent(List<Member> withdrawnMembers) {
        this.withdrawnMembers = withdrawnMembers;
    }

    public List<Member> getWithdrawnMembers() {
        return withdrawnMembers;
    }

}
