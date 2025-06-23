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

    // 생성자 의존 주입 - DI 처리
    private final BoardPersistRepository br;

    // 주소 설계 : /board/{id}/delete
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id) {
        br.deleteById(id);

        return "redirect:/";
    }

    // 주소 설계 : http://localhost:8080/board/{id}/update-form
    // 화면 요청 : GET
    // @return update-form.mustache
    // @param : id (board_pk)
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        Board board = br.findById(id);

        request.setAttribute("board", board);
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long id, BoardRequest.UpdateDTO reqDTO) {

        // 트랜잭션
        // 수정 -- select - 값을 확인해서 - 데이터를 수정 --> update
        // JPA 영속성 컨텍스트 활용
        br.update(id, reqDTO);
        // 수정 전략을 Dirty Checking 을 활용
        // 장점
        // 1. UPDATE 쿼리 자동 생성
        // 2. 변경된 필드만 업데이트 (성능 최적화)
        // 3. 영속성 컨텍스트에 일관성 유지
        // 4. 1차 캐시 자동 갱신

        return "redirect:/";
    }

    // 게시글 상세 보기
    // 주소설계 GET : http://localhost:8080/board/3
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        // 1차 캐시 효과 - DB에 접근하지 않고 바로 영속성 컨텍스트에서 꺼낸다.
        Board board = br.findById(id);

        request.setAttribute("board", board);

        return "board/detail";
    }

    // 1. index.mustache 파일을 반환 시키는 기능을 만든다.
    // 주소 설계 : http://localhost:8080/ , http://localhost:8080/index
    @GetMapping({"/","/index"})
    public String boardList(HttpServletRequest request) {

        List<Board> boardList = br.findAll();
        request.setAttribute("boardList",boardList);
        return "index";
    }

    // 게시글 작성 화면 요청 처리
    @GetMapping("/board/save-form")
    public String saveForm() {
        return "board/save-form";
    }

    // 게시글 작성 액션(수행) 처리
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO) {
        // HTTP 요청 본문 : title=값&content=값&username=값
        // form 태그의 MIME 타입 (application/x-www-form-urlencoded)

        // reqDTO <-- 사용자가 던진 데이터가 있음
        // DTO -- Board -- DB
        // Board board = new Board(reqDTO.getTitle(), reqDTO.getContent(), reqDTO.getUsername());
        Board board = reqDTO.toEntity();
        br.save(board);

        return "redirect:/";
    }


}