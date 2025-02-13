package njb.recipe.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${mail.username}")
    private String from;

    @Value("${app.domain}")
    private String domain;


    /**
     * 회원 활성화 인증 링크를 포함한 이메일 발송 메서드
     * @param to 수신자 이메일
     * @param activationToken 회원 활성화 토큰
     */
    public void sendEmail(String to, String activationToken){
        String activationLink = domain + "/auth/activate?token=" + activationToken;
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "[Recipe] 회원가입 인증 메일";

            // HTML 형식의 이메일 내용 구성
            String htmlContent = "<html><body>"
                    + "<p>안녕하세요,</p>"
                    + "<p>회원가입을 완료하기 위해 아래 링크를 클릭해주세요:</p>"
                    + "<p><a href='" + activationLink + "'>" + activationLink + "</a></p>"
                    + "<p>감사합니다.</p>"
                    + "</body></html>";

            // 두 번째 파라미터를 true로 설정하여 HTML 형식임을 지정
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }
}
