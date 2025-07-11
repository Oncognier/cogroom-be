package oncog.cogroom.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "EMAIL_VERIFICATION")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String verifyCode;

    @Column(nullable = false)
    private boolean verifyStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime expireDate;

    public void updateStatusToTrue() {
        this.verifyStatus = true;
    }
    public void updateStatusToFalse() {
        this.verifyStatus = false;
    }

    public void updateCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public void updateExpireDate() {
        this.expireDate = LocalDateTime.now().plusMinutes(10);
    }
}
