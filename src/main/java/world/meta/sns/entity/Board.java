package world.meta.sns.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import world.meta.sns.enums.Category;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments;

    public Board(String title, String content, Category category, Member member) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setBoard(this);
    }

    public void update(Board board) {
        if (StringUtils.isNotBlank(board.getTitle())) {
            this.title = board.getTitle();
        }

        if (StringUtils.isNotBlank(board.getContent())) {
            this.content = board.getContent();
        }

        if (board.getCategory() != null) {
            this.category = board.getCategory();
        }
    }
}
