package com.tenco.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller // IoC 대상 - 싱글톤 패턴으로 관리 됨
public class BoardController {

    private final BoardRepository  boardRepository;

    @GetMapping("/")
    public String index(HttpServletRequest request) {

        // 1. 게시글 목록 조회
        List<Board> boardList = boardRepository.findByAll();

        // 2. 생각해볼 사항 - Board 엔티티에는 User 엔티티와 연관관계 중
        // 연관관계 확인
        //boardList.get(0).getUser().getUsername();

        request.setAttribute("boardList", boardList);
        return "index";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        Board board = boardRepository.findById(id);
        request.setAttribute("board", board);

        return "board/detail";
    }

}