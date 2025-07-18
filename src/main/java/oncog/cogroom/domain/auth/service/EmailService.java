package oncog.cogroom.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oncog.cogroom.domain.auth.dto.request.AuthRequest;
import oncog.cogroom.domain.auth.entity.EmailVerification;
import oncog.cogroom.domain.auth.exception.AuthErrorCode;
import oncog.cogroom.domain.auth.exception.AuthException;
import oncog.cogroom.domain.auth.repository.EmailRepository;
import oncog.cogroom.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailService {
    private final EmailRepository emailRepository;
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.email-link-url}")
    private String emailLinkUrl;

    @Async
    public void sendAuthCodeEmail(AuthRequest.EmailDTO request) throws MessagingException{
        String toEmail = request.getEmail();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        Context context = new Context();
        context.setVariable("verifyLink",generateVerificationLink(toEmail));

        helper.setTo(toEmail); // 목적지
        helper.setSubject("[코그룸/회원가입] 인증 링크"); // 타이틀
        helper.setFrom(fromEmail); // 발신 이메일
        helper.setText(templateEngine.process("email-page", context), true);
        mailSender.send(message);
     }

    @Async
    public void sendWelcomeEmail(String email) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        Resource resource = new ClassPathResource("templates/welcome-page.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        helper.setTo(email); // 목적지
        helper.setSubject("\uD83D\uDCA7안녕, 코그니어!"); // 타이틀
        helper.setText(html, true);
        helper.setFrom(fromEmail); // 발신 이메일
        mailSender.send(message);
    }

    private void saveEmail(String toEmail, String verificationCode) {
        Optional<EmailVerification> emailVerificationOpt = emailRepository.findByEmail(toEmail);

        // 이미 전송된 이메일 링크가 존재하는 경우 코드와 만료 시간 업데이트, 아닌 경우 새로 객체 생성
        emailVerificationOpt.ifPresentOrElse(
                value -> {
                    EmailVerification emailVerification = emailVerificationOpt.get();
                    emailVerification.updateCode(verificationCode);
                    emailVerification.updateStatusToFalse();
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

    // 이메일 중복 검사
    public void existEmail(String toEmail) {
        if(Boolean.TRUE.equals(memberRepository.existsByEmail(toEmail))) throw new AuthException(AuthErrorCode.EMAIL_DUPLICATE_ERROR);
    }

    // 이메일의 인증 상태 반환
    public boolean verifiedEmail(String email) {
        return emailRepository.existsByEmailAndVerifyStatus(email,true);
    }

    // 이메일 인증 유무 검사
    public void isVerified(String email) {
        Boolean isVerified = emailRepository.existsByEmailAndVerifyStatus(email, true);

        if(Boolean.FALSE.equals(isVerified)) throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_ERROR);
    }

    // 이메일 인증 (boolean 형으로 변경 예정)
    public void verifyCode(String userEmail, String verificationCode) {
        Optional<EmailVerification> byEmailAndVerifyCode = emailRepository.findByEmailAndVerifyCode(userEmail, verificationCode);

        byEmailAndVerifyCode.ifPresent(emailVerification -> {
            // 링크 시간이 만료된 경우
            if(LocalDateTime.now().isAfter(emailVerification.getExpireDate())){
                throw new AuthException(AuthErrorCode.LINK_EXPIRED_ERROR);
            }

            // 링크 시간이 만료되지 않았으면 인증
            emailVerification.updateStatusToTrue();
        });
    }

    // 인증 링크 생성 및 이메일 인증 객체 저장
    private String generateVerificationLink(String toEmail) {
        int verificationCode = generateVerificationCode();

        saveEmail(toEmail, String.valueOf(verificationCode));

        return String.format("%s?userEmail=%s&verificationCode=%s", emailLinkUrl, toEmail ,verificationCode);
    }

    // 인증 코드 생성
    private int generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        return 100000 + secureRandom.nextInt(900000);
    }

    public void clearNotVerifiedEmail() {
        List<EmailVerification> notVerifiedEmails = emailRepository.findAllByVerifyStatus(false);

        notVerifiedEmails.stream()
                .forEach(email -> {
                    if (LocalDateTime.now().isAfter(email.getExpireDate())) {
                        emailRepository.delete(email);
                    }
                });
    }
}
