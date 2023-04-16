package world.meta.sns.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String memberName;

    @JsonManagedReference
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
