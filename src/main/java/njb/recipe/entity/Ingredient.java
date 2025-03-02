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
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 컬럼 이름
    private Member member; // 재료의 소유자

    @ManyToOne
    @JoinColumn(name = "refrigerator_id", nullable = false)
    private Refrigerator refrigerator; // 이 재료가 속한 냉장고

    @Column(name = "ingredient_name", length = 100, nullable = false)
    private String name; // 재료 이름

    @Column(name = "photo_url")
    private String photoUrl; // 재료 사진 URL

    @Column(name = "quantity", nullable = false)
    private int quantity; // 갯수

    @Column(name = "category", nullable = false)
    private String category; // 분류 (예: 육류, 채소 등)

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate; // 등록 날짜

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate; // 유통기한
}
