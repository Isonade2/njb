package njb.recipe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", length = 50, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private JoinType joinType;

    @Column(nullable = false)
    private boolean activated = false;

//    @ManyToMany
//    @JoinTable(
//            name = "member_authority",
//            joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
//            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
//    private Set<Authority> authorities;


    private String role;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<RefreshToken> refreshToken;



    public Member(Long id) {
        this.id = id; //Long타입 생성자 추가
    }

    public void updateRefreshToken(RefreshToken token){
        this.refreshToken.add(token);
    }

    public void activate(){
        this.activated = true;
    }

    public void updateRole(String role){
        this.role = role;
    }

    public void updateJoinType(JoinType joinType){
        this.joinType = joinType;
    }

    // fcm 토큰 용 추가 2개
    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    public void updateFcmToken(String token) {
        this.fcmToken = token;
    }
}
