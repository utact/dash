package com.ssafy.dash.board.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.dash.board.domain.Board;
import com.ssafy.dash.board.dto.BoardCreateRequest;
import com.ssafy.dash.board.dto.BoardResponse;
import com.ssafy.dash.board.dto.BoardUpdateRequest;
import com.ssafy.dash.board.mapper.BoardMapper;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BoardService boardService;

    private User user;
    private Board board;

    @BeforeEach
    void setUp() {
        user = new User(1L, "tester", "test@example.com", LocalDateTime.now());
        board = new Board(1L, "Test Title", "Test Content", 1L, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createBoard_Success() {
        // given
        BoardCreateRequest req = new BoardCreateRequest("Test Title", "Test Content", 1L);
        given(userMapper.selectById(1L)).willReturn(user);
        
        // when
        BoardResponse response = boardService.create(req);

        // then
        verify(boardMapper).insert(any(Board.class));
        assertThat(response.getTitle()).isEqualTo(req.getTitle());
        assertThat(response.getAuthorName()).isEqualTo(user.getUsername());
    }

    @Test
    void findById_Success() {
        // given
        given(boardMapper.selectById(1L)).willReturn(board);
        given(userMapper.selectById(1L)).willReturn(user);

        // when
        BoardResponse response = boardService.findById(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAuthorName()).isEqualTo(user.getUsername());
    }

    @Test
    void findAll_Success() {
        // given
        given(boardMapper.selectAll()).willReturn(List.of(board));
        given(userMapper.selectById(1L)).willReturn(user);

        // when
        List<BoardResponse> responses = boardService.findAll();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAuthorName()).isEqualTo(user.getUsername());
    }

    @Test
    void updateBoard_Success() {
        // given
        BoardUpdateRequest req = new BoardUpdateRequest("Updated Title", "Updated Content");
        given(boardMapper.selectById(1L)).willReturn(board);
        given(userMapper.selectById(1L)).willReturn(user);

        // when
        BoardResponse response = boardService.update(1L, req);

        // then
        verify(boardMapper).update(any(Board.class));
        assertThat(response.getTitle()).isEqualTo(req.getTitle());
    }

    @Test
    void deleteBoard_Success() {
        // given
        given(boardMapper.delete(1L)).willReturn(1);

        // when
        boardService.delete(1L);

        // then
        verify(boardMapper).delete(1L);
    }

}
