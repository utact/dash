package com.ssafy.dash.like.application;

import com.ssafy.dash.board.domain.Board;
import com.ssafy.dash.board.domain.BoardRepository;
import com.ssafy.dash.board.domain.exception.BoardNotFoundException;
import com.ssafy.dash.comment.domain.Comment;
import com.ssafy.dash.comment.domain.CommentRepository;
import com.ssafy.dash.comment.domain.exception.CommentNotFoundException;
import com.ssafy.dash.common.fixtures.BoardFixtures;
import com.ssafy.dash.common.fixtures.CommentFixtures;
import com.ssafy.dash.common.fixtures.UserFixtures;
import com.ssafy.dash.like.domain.BoardLike;
import com.ssafy.dash.like.domain.CommentLike;
import com.ssafy.dash.like.domain.LikeRepository;
import com.ssafy.dash.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeService 단위 테스트")
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private LikeService likeService;

    private User user;
    private Board board;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = UserFixtures.createUser();
        board = BoardFixtures.createBoard(user);
        comment = CommentFixtures.createComment(board.getId(), user);
    }

    @Nested
    @DisplayName("likeBoard 메서드")
    class LikeBoardTests {

        @Test
        @DisplayName("게시글에 추천하면 likeCount가 증가한다")
        void likeBoard_ShouldIncrementLikeCount() {
            given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
            given(likeRepository.findBoardLike(board.getId(), user.getId())).willReturn(Optional.empty());

            int result = likeService.likeBoard(board.getId(), user.getId());

            assertThat(result).isEqualTo(1);
            verify(likeRepository).saveBoardLike(any(BoardLike.class));
            verify(boardRepository).update(board);
        }

        @Test
        @DisplayName("이미 추천한 게시글에 다시 추천하면 likeCount가 변경되지 않는다")
        void likeBoard_WhenAlreadyLiked_ShouldNotChange() {
            BoardLike existingLike = BoardLike.create(board.getId(), user.getId(), null);
            given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
            given(likeRepository.findBoardLike(board.getId(), user.getId())).willReturn(Optional.of(existingLike));

            int result = likeService.likeBoard(board.getId(), user.getId());

            assertThat(result).isEqualTo(0);
            verify(likeRepository, never()).saveBoardLike(any(BoardLike.class));
        }

        @Test
        @DisplayName("존재하지 않는 게시글에 추천하면 예외가 발생한다")
        void likeBoard_WhenBoardNotExists_ShouldThrowException() {
            given(boardRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.likeBoard(999L, user.getId()))
                    .isInstanceOf(BoardNotFoundException.class);

            verify(likeRepository, never()).saveBoardLike(any(BoardLike.class));
        }
    }

    @Nested
    @DisplayName("unlikeBoard 메서드")
    class UnlikeBoardTests {

        @Test
        @DisplayName("게시글 추천을 취소하면 likeCount가 감소한다")
        void unlikeBoard_ShouldDecrementLikeCount() {
            board.incrementLikeCount(); // 미리 1 증가
            BoardLike existingLike = BoardLike.create(board.getId(), user.getId(), null);

            given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
            given(likeRepository.findBoardLike(board.getId(), user.getId())).willReturn(Optional.of(existingLike));

            int result = likeService.unlikeBoard(board.getId(), user.getId());

            assertThat(result).isEqualTo(0);
            verify(likeRepository).deleteBoardLike(board.getId(), user.getId());
            verify(boardRepository).update(board);
        }

        @Test
        @DisplayName("추천하지 않은 게시글의 추천을 취소하면 변경되지 않는다")
        void unlikeBoard_WhenNotLiked_ShouldNotChange() {
            given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
            given(likeRepository.findBoardLike(board.getId(), user.getId())).willReturn(Optional.empty());

            int result = likeService.unlikeBoard(board.getId(), user.getId());

            assertThat(result).isEqualTo(0);
            verify(likeRepository, never()).deleteBoardLike(board.getId(), user.getId());
        }
    }

    @Nested
    @DisplayName("likeComment 메서드")
    class LikeCommentTests {

        @Test
        @DisplayName("댓글에 추천하면 likeCount가 증가한다")
        void likeComment_ShouldIncrementLikeCount() {
            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
            given(likeRepository.findCommentLike(comment.getId(), user.getId())).willReturn(Optional.empty());

            int result = likeService.likeComment(comment.getId(), user.getId());

            assertThat(result).isEqualTo(1);
            verify(likeRepository).saveCommentLike(any(CommentLike.class));
            verify(commentRepository).update(comment);
        }

        @Test
        @DisplayName("존재하지 않는 댓글에 추천하면 예외가 발생한다")
        void likeComment_WhenCommentNotExists_ShouldThrowException() {
            given(commentRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.likeComment(999L, user.getId()))
                    .isInstanceOf(CommentNotFoundException.class);

            verify(likeRepository, never()).saveCommentLike(any(CommentLike.class));
        }
    }

    @Nested
    @DisplayName("unlikeComment 메서드")
    class UnlikeCommentTests {

        @Test
        @DisplayName("댓글 추천을 취소하면 likeCount가 감소한다")
        void unlikeComment_ShouldDecrementLikeCount() {
            comment.incrementLikeCount();
            CommentLike existingLike = CommentLike.create(comment.getId(), user.getId(), null);

            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
            given(likeRepository.findCommentLike(comment.getId(), user.getId())).willReturn(Optional.of(existingLike));

            int result = likeService.unlikeComment(comment.getId(), user.getId());

            assertThat(result).isEqualTo(0);
            verify(likeRepository).deleteCommentLike(comment.getId(), user.getId());
            verify(commentRepository).update(comment);
        }
    }

    @Nested
    @DisplayName("isLiked 메서드")
    class IsLikedTests {

        @Test
        @DisplayName("게시글 추천 여부를 확인할 수 있다")
        void isLikedBoard_ShouldReturnCorrectStatus() {
            BoardLike existingLike = BoardLike.create(board.getId(), user.getId(), null);
            given(likeRepository.findBoardLike(board.getId(), user.getId())).willReturn(Optional.of(existingLike));

            boolean result = likeService.isLikedBoard(board.getId(), user.getId());

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("댓글 추천 여부를 확인할 수 있다")
        void isLikedComment_ShouldReturnCorrectStatus() {
            given(likeRepository.findCommentLike(comment.getId(), user.getId())).willReturn(Optional.empty());

            boolean result = likeService.isLikedComment(comment.getId(), user.getId());

            assertThat(result).isFalse();
        }
    }

}
