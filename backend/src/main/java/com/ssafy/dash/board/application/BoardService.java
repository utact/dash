package com.ssafy.dash.board.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.dash.board.application.dto.command.BoardCreateCommand;
import com.ssafy.dash.board.application.dto.result.BoardResult;
import com.ssafy.dash.board.application.dto.command.BoardUpdateCommand;
import com.ssafy.dash.board.domain.Board;
import com.ssafy.dash.board.domain.BoardRepository;
import com.ssafy.dash.board.domain.exception.BoardNotFoundException;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.domain.UserRepository;
import com.ssafy.dash.user.domain.exception.UserNotFoundException;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BoardResult create(BoardCreateCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId()));

        LocalDateTime now = LocalDateTime.now();
        Board board = Board.create(command.getUserId(), command.getTitle(), command.getContent(), now);

        boardRepository.save(board);

        return toResult(board, user);
    }

    @Transactional(readOnly = true)
    public BoardResult findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));
        
        User user = userRepository.findById(board.getUserId()).orElse(null);

        return toResult(board, user);
    }

    @Transactional(readOnly = true)
    public List<BoardResult> findAll() {
        
        return boardRepository.findAll().stream()
                .map(board -> {
                    User user = userRepository.findById(board.getUserId()).orElse(null);
                    return toResult(board, user);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public BoardResult update(Long id, BoardUpdateCommand command) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));

        LocalDateTime now = LocalDateTime.now();
        board.applyUpdate(command.getTitle(), command.getContent(), now);

        boardRepository.update(board);
        
        User user = userRepository.findById(board.getUserId()).orElse(null);

        return toResult(board, user);
    }

    @Transactional
    public void delete(Long id) {
        if (boardRepository.findById(id).isEmpty()) {
            throw new BoardNotFoundException(id);
        }
        boardRepository.delete(id);
    }

    private BoardResult toResult(Board board, User user) {
        String authorName = (user != null) ? user.getUsername() : "Unknown";
        
        return BoardResult.from(board, authorName);
    }

}
