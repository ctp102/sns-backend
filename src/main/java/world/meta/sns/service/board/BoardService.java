package world.meta.sns.service.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.dto.board.BoardDto;
import world.meta.sns.entity.Board;
import world.meta.sns.entity.Member;
import world.meta.sns.form.board.BoardForm;
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.member.MemberRepository;
import world.meta.sns.service.member.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * 게시글 목록 조회
     *
     * @param boardForm the board form
     * @param pageable  the pageable
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<BoardDto> findBoardList(BoardForm boardForm, Pageable pageable) {
        Page<BoardDto> pageBoardDtos = boardRepository.findAll(boardForm, pageable);

        // TODO: [2023-04-17] 개수가 많은 경우에는 stream.parallel() 처리 고려
        pageBoardDtos.getContent().forEach(boardDto -> {
            Board foundBoard = boardRepository.findById(boardDto.getBoardId())
                    .orElseThrow(() -> new IllegalStateException("해당 게시글이 존재하지 않습니다."));

            BoardDto.setCommentDtos(foundBoard, boardDto);
        });

        return pageBoardDtos;
    }

    /**
     * 게시글 단건 조회
     *
     * @param boardId the board id
     * @return the board dto
     */
    @Transactional(readOnly = true)
    public BoardDto findBoard(Long boardId) {

        Board foundBoard = boardRepository.findFetchJoinById(boardId);

        if (foundBoard == null) {
            throw new IllegalStateException("해당 게시글이 존재하지 않습니다.");
        }

        BoardDto boardDto = BoardDto.from(foundBoard);

        BoardDto.setCommentDtos(foundBoard, boardDto);

        return boardDto;
    }

    /**
     * 게시글 저장
     *
     * @param memberId the member id
     * @param board    the board
     * @return the board
     */
    public Board saveBoard(Long memberId, Board board) {

        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        foundMember.addBoard(board);

        return boardRepository.save(board);
    }

    /**
     * 게시글 수정
     *
     * @param boardId the board id
     * @param board   the board
     */
    public void updateBoard(Long boardId, Board board) {

        Board foundBoard = boardRepository.findById(boardId).orElseThrow();
        foundBoard.update(board);
    }

    /**
     * 게시글 삭제
     *
     * @param boardId the board id
     */
    public void deleteBoard(Long boardId) {

        Board foundBoard = boardRepository.findById(boardId).orElseThrow();

        if (!foundBoard.getId().equals(boardId)) {
            throw new IllegalStateException("해당 게시글이 존재하지 않습니다.");
        }

        boardRepository.delete(foundBoard);
    }

}
