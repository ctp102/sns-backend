package world.meta.sns.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import world.meta.sns.dto.member.MemberSaveDto;
import world.meta.sns.dto.member.MemberUpdateDto;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String memberEmail;

    @Column(nullable = false)
    private String memberName;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @BatchSize(size = 500)
    private List<Board> boards;

    public Member(String memberName) {
        this.memberName = memberName;
    }

    public Member(String memberEmail, String memberName) {
        this.memberEmail = memberEmail;
        this.memberName = memberName;
    }

    public void addBoard(Board board) {
        this.boards.add(board);
        board.setMember(this);
    }

    public void update(MemberUpdateDto memberUpdateDto) {
        this.memberName = memberUpdateDto.getMemberName();
    }

    public static Member from(MemberSaveDto memberSaveDto) {
        return Member.builder()
                .memberEmail(memberSaveDto.getMemberEmail())
                .memberName(memberSaveDto.getMemberName())
                .build();
    }

}
