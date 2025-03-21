package njb.recipe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Refrigerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refrigerator_id", nullable = false)
    private Long id;

    @Column(name = "refrigerator_name", length = 100, nullable = false)
    private String name;

    @Column(name = "photo_url")
    private String photoUrl; // 냉장고 사진 URL

    @Column(name = "description")
    private String description; // 세부 설명

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 컬럼 이름
    private Member member; // 이 냉장고의 소유자

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일자

    public Refrigerator(Long id) {
        this.id = id;
    }
}

