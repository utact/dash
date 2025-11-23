package com.ssafy.dash.board.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.dash.board.dto.BoardCreateRequest;
import com.ssafy.dash.board.dto.BoardResponse;
import com.ssafy.dash.board.dto.BoardUpdateRequest;
import com.ssafy.dash.board.service.BoardService;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBoard_Success() throws Exception {
        BoardCreateRequest req = new BoardCreateRequest("Title", "Content", 1L);
        BoardResponse res = new BoardResponse(1L, "Title", "Content", 1L, "User", LocalDateTime.now(), LocalDateTime.now());

        given(boardService.create(any(BoardCreateRequest.class))).willReturn(res);

        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void getAllBoards_Success() throws Exception {
        BoardResponse res = new BoardResponse(1L, "Title", "Content", 1L, "User", LocalDateTime.now(), LocalDateTime.now());
        given(boardService.findAll()).willReturn(List.of(res));

        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getBoardById_Success() throws Exception {
        BoardResponse res = new BoardResponse(1L, "Title", "Content", 1L, "User", LocalDateTime.now(), LocalDateTime.now());
        given(boardService.findById(1L)).willReturn(res);

        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateBoard_Success() throws Exception {
        BoardUpdateRequest req = new BoardUpdateRequest("Updated", "Updated Content");
        BoardResponse res = new BoardResponse(1L, "Updated", "Updated Content", 1L, "User", LocalDateTime.now(), LocalDateTime.now());

        given(boardService.update(eq(1L), any(BoardUpdateRequest.class))).willReturn(res);

        mockMvc.perform(put("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteBoard_Success() throws Exception {
        mockMvc.perform(delete("/api/boards/1"))
                .andExpect(status().isNoContent());
    }
    
}
