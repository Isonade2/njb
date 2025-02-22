package njb.recipe.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    private String deviceInfo;
    private Boolean autoLogin = false;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;



    public RefreshToken updateValue(String value) {
        this.value = value;
        return this;
    }

    public void updateAutoLogin(Boolean isAutoLogin){
        this.autoLogin = isAutoLogin;
    }

}
