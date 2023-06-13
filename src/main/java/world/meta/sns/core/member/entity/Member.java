package world.meta.sns.core.member.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import world.meta.sns.core.member.dto.MemberJoinDto;
import world.meta.sns.core.member.dto.MemberUpdateDto;
import world.meta.sns.api.common.entity.BaseTimeEntity;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.member.enums.RoleTypes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ColumnDefault("'TEMP'")
    private String name;

    @Column(nullable = false)
    @ColumnDefault("'USER'")
    @Enumerated(EnumType.STRING)
    private RoleTypes role;

    private String provider;   // google, facebook...
    private String providerId; // oauth2 로그인하면 받는 id

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @BatchSize(size = 500)
    private List<Board> boards = new ArrayList<>();

    public Member(String name) {
        this.name = name;
    }

    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public void addBoard(Board board) {
        this.boards.add(board);
        board.setMember(this);
    }

    public void update(MemberUpdateDto memberUpdateDto) {
        this.name = memberUpdateDto.getName();
    }

    public static Member from(MemberJoinDto memberJoinDto) {
        return Member.builder()
                .email(memberJoinDto.getEmail())
                .password(memberJoinDto.getPassword())
                .role(memberJoinDto.getRole())
                .name("하드코딩")
                .build();
    }

}
