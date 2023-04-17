package world.meta.sns.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import world.meta.sns.dto.board.BoardRequestDto;
import world.meta.sns.enums.Category;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 500)
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

    // BoardRequestDto를 만들어서 넣던데 왜 이렇게 하지?
    // 여기서 null 체크를 하는게 맞을까?
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

    public static Board from(BoardRequestDto boardRequestDto) {
        return Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .category(boardRequestDto.getCategory())
                .build();
    }

}
