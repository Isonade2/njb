package njb.recipe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Long id; // 재료 ID

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false) 
    private Member member; // 재료의 소유자

    @ManyToOne
    @JoinColumn(name = "refrigerator_id", nullable = false)
    private Refrigerator refrigerator; // 이 재료가 속한 냉장고

    @ManyToOne 
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "ingredient_name", length = 100, nullable = false)
    private String name; // 재료 이름

    @Column(name = "photo_url")
    private String photoUrl; // 재료 사진 URL

    @Column(name = "quantity", nullable = false)
    private int quantity; // 갯수


    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate; // 등록 날짜

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate; // 유통기한

    // FCM 토큰을 가져오는 메서드 추가
    public String getUserFcmToken() {
        return this.member.getFcmToken();
    }
}
