package world.meta.sns.aaaaaa.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.aaaaaa.board.dto.BoardDto;
import world.meta.sns.aaaaaa.board.dto.BoardRequestDto;
import world.meta.sns.aaaaaa.board.dto.BoardUpdateDto;
import world.meta.sns.aaaaaa.board.entity.Board;
import world.meta.sns.aaaaaa.member.entity.Member;
import world.meta.sns.aaaaaa.board.form.BoardForm;
import world.meta.sns.aaaaaa.board.repository.BoardRepository;
import world.meta.sns.aaaaaa.comment.repository.CommentRepository;
import world.meta.sns.aaaaaa.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

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

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("해당 회원이 존재하지 않습니다."));

        foundMember.addBoard(board);

        return boardRepository.save(board);
    }

    /**
     * Save board board.
     *
     * @param requestDto the request dto
     * @return the board
     */
    public Board saveBoard(BoardRequestDto requestDto) {

        Member foundMember = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalStateException("해당 회원이 존재하지 않습니다."));

        Board board = Board.from(requestDto);
        foundMember.addBoard(board);

        return boardRepository.save(board);
    }

    /**
     * 게시글 수정
     *
     * @param boardId        the board id
     * @param boardUpdateDto the board update dto
     */
    public void updateBoard(Long boardId, BoardUpdateDto boardUpdateDto) {

        Board board = boardRepository.findById(boardId).orElse(null);

        if (board == null) {
            // 응답값 변경하기(404: 찾을 수 없음이라는 둥...)
            return;
        }

        board.update(boardUpdateDto);
    }

    /**
     * 게시글 삭제
     * 작성된 댓글도 함께 삭제
     *
     * @param boardId the board id
     */
    public void deleteBoard(Long boardId) {

        List<Long> parentCommentIds = commentRepository.findParentCommentIdsByBoardId(boardId);
        commentRepository.deleteChildCommentsByParentCommentIds(parentCommentIds); // 자식 댓글부터 삭제해야 참조 무결성이 깨지지 않는다.
        commentRepository.deleteCommentsByBoardId(boardId); // 부모 댓글 삭제

        boardRepository.deleteByBoardId(boardId);
    }

}
