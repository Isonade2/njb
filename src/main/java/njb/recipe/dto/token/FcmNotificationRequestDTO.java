package njb.recipe.dto.token;

public class FcmNotificationRequestDTO {
    private String fcmToken;
    private String title;
    private String body;

    // 생성자
    public FcmNotificationRequestDTO(String fcmToken, String title, String body) {
        this.fcmToken = fcmToken;
        this.title = title;
        this.body = body;
    }

    // 기본 생성자 추가
    public FcmNotificationRequestDTO() {
        // 필드 초기화가 필요하다면 여기에 추가
    }

    // Getter 및 Setter
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
