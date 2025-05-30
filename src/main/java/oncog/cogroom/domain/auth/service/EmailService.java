package oncog.cogroom.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import oncog.cogroom.domain.auth.entity.EmailVerification;
import oncog.cogroom.domain.auth.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.email-link-url}")
    private String emailLinkUrl;

    public void sendEmail(String toEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(toEmail); // 목적지
        helper.setSubject(generateVerificationLink(toEmail)); // 타이틀
        helper.setFrom(fromEmail); // 발신 이메일
        mailSender.send(message);

    }

    private void saveEmail(String toEmail, int verificationCode) {
        emailRepository.save(EmailVerification.builder()
                .email(toEmail)
                .verifyCode(String.valueOf(verificationCode))
                .verifyStatus(false)
                .expireDate(LocalDateTime.now().plusMinutes(10))
                .build());
    }

    // 인증 링크 생성 및 이메일 인증 객체 저장
    private String generateVerificationLink(String toEmail) {
        int verificationCode = generateVerificationCode();

        saveEmail(toEmail, verificationCode);

        return String.format("%s/api/v1/auth/%s 링크를 10분 이내에 클릭해주세요.", emailLinkUrl, verificationCode);
    }


    // 인증 코드 생성
    private int generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        return 100000 + secureRandom.nextInt(900000);
    }
}
