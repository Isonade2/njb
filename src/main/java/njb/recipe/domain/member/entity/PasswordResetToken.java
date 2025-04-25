package njb.recipe.domain.member.entity;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash(value = "passwordResetToken", timeToLive = 60 * 60)
@Getter
@Builder
public class PasswordResetToken {
    @Id
    private String id;
    private Long memberId;
}
