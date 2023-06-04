package world.meta.sns.core.member.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import world.meta.sns.core.member.dto.MemberJoinDto;
import world.meta.sns.core.member.dto.MemberUpdateDto;
import world.meta.sns.api.common.entity.BaseTimeEntity;
import world.meta.sns.core.board.entity.Board;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false) // TODO: [2023-05-27] false로 바꾸기
    private String password;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true) // TODO: [2023-05-27] false로 바꾸기
    private String role; // ROLE_USER, ROLE_ADMIN

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
//                .name(memberJoinDto.getName())
                .build();
    }

}