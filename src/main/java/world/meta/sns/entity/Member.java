package world.meta.sns.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
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

}
