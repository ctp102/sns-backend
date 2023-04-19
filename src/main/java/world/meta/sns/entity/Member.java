package world.meta.sns.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import world.meta.sns.dto.member.MemberRequestDto;

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

    private String memberName;

    @OneToMany(mappedBy = "member")
    @BatchSize(size = 500)
    private List<Board> boards;

    public Member(String memberName) {
        this.memberName = memberName;
    }

    public void addBoard(Board board) {
        this.boards.add(board);
        board.setMember(this);
    }

    public static Member from(MemberRequestDto requestDto) {
        return Member.builder()
                .memberName(requestDto.getMemberName())
                .build();
    }

}
