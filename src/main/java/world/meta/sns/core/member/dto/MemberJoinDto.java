package world.meta.sns.core.member.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.meta.sns.core.member.enums.RoleTypes;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJoinDto {

    private String email;
    private String password;
    private RoleTypes role;

    public MemberJoinDto(String email, String password, RoleTypes role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public MemberJoinDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
