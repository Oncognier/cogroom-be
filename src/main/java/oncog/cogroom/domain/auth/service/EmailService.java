package oncog.cogroom.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.entity.EmailVerification;
import oncog.cogroom.domain.auth.repository.EmailRepository;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailService {
    private final EmailRepository emailRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.email-link-url}")
    private String emailLinkUrl;

    public void sendEmail(String toEmail) throws MessagingException {
        if (!existEmail(toEmail)) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(toEmail); // 목적지
            helper.setSubject("Oncognier auth email"); // 타이틀
            helper.setText(generateVerificationLink(toEmail));
            helper.setFrom(fromEmail); // 발신 이메일
            mailSender.send(message);
        }else{
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    private void saveEmail(String toEmail, String verificationCode) {
        Optional<EmailVerification> emailVerificationOpt = emailRepository.findByEmail(toEmail);

        // 이미 전송된 이메일 링크가 존재하는 경우 코드와 만료 시간 업데이트, 아닌 경우 새로 객체 생성
        emailVerificationOpt.ifPresentOrElse(
                value -> {
                    EmailVerification emailVerification = emailVerificationOpt.get();
                    emailVerification.updateCode(verificationCode);
                    emailVerification.updateExpireDate();
                },
                () -> {
                    emailRepository.save(EmailVerification.builder()
                            .email(toEmail)
                            .verifyCode(String.valueOf(verificationCode))
                            .verifyStatus(false)
                            .expireDate(LocalDateTime.now().plusMinutes(10))
                            .build());
                }
        );
    }

    // 사용중인 이메일인 경우 true 반환
    public boolean existEmail(String toEmail) {
        return memberRepository.existsByEmail(toEmail);
    }

    // 이메일의 인증 상태 반환
    public boolean verifiedEmail(String toEmail) {
        return emailRepository.existsByEmailAndVerifyStatus(toEmail,true);
    }

    // 이메일 인증 (boolean 형으로 변경 예정)
    public void verifyCode(String userEmail, String verificationCode) {
        Optional<EmailVerification> byEmailAndVerifyCode = emailRepository.findByEmailAndVerifyCode(userEmail, verificationCode);

        byEmailAndVerifyCode.ifPresent(emailVerification -> {
            // 링크 시간이 만료된 경우
            if(LocalDateTime.now().isAfter(emailVerification.getExpireDate())){
                throw new IllegalArgumentException("링크 시간 만료");
            }

            // 링크 시간이 만료되지 않았으면 인증
            emailVerification.updateStatus();
        });
    }

    // 인증 링크 생성 및 이메일 인증 객체 저장
    private String generateVerificationLink(String toEmail) {
        int verificationCode = generateVerificationCode();

        saveEmail(toEmail, String.valueOf(verificationCode));

        return String.format("%s/api/v1/auth/check-verification?userEmail=%s&verificationCode=%s 링크를 10분 이내에 클릭해주세요.", emailLinkUrl, toEmail ,verificationCode);
    }

    // 인증 코드 생성
    private int generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        return 100000 + secureRandom.nextInt(900000);
    }
}
