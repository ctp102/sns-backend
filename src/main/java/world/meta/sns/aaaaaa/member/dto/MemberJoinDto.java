package world.meta.sns.aaaaaa.member.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class MemberJoinDto {

    private String email;
    private String password;
    private String role;

    public MemberJoinDto(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
