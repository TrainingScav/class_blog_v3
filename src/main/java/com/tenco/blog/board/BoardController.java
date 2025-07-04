package com.tenco.blog.board;

import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller // IoC 대상 - 싱글톤 패턴으로 관리 됨
public class BoardController {

    private final BoardRepository boardRepository;



    // 게시글 수정하기 화면 요청

    // 1. 인증 검사
    // 2. 수정할 게시글 존재 여부 확인
    // 3. 권한 체크
    // 4. 수정 폼에 기존 데이터 뷰 바인딩 처리
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long boardId, HttpServletRequest request, HttpSession session) {

        // 1.
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        // 2.
        Board board = boardRepository.findById(boardId);
        if (board == null) {
            throw new RuntimeException("수정할 게시글이 존재하지 않습니다.");
        }

        // 3.
        if( !board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 4.
        request.setAttribute("board", board);

        // 내부에서(스프링 컨테이너) 뷰 리졸브를 활용해서 머스태치 파일 요청
        return "board/update-form";
    }


    // 1. 인증 검사
    // 2. 유효성 검사
    // 3. 권한 체크를 위해 게시글 다시 조회
    // 4. 더티 체킹을 통한 수정 설정
    // 5. 수정 완료 후에 게시글 상세보기로 리다이렉트 처리

    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long boardId, BoardRequest.UpdateDTO reqDTO, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        // 2. 사용자 입력값 유효성 검사
        reqDTO.validate();

        // 3. 권한 체크를 위한 조회
        Board board = boardRepository.findById(boardId);
        // board - 1차 캐시에 들어가 있음

        if (!board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 4. 엔티티 접근해서 상태 변경
        Board updateBoard = boardRepository.updateById(boardId, reqDTO);

        return "redirect:/board/" + boardId;
    }

    /**
     * 게시글 삭제 액션 처리
     * @param id
     * @param session
     * @return 리스트 화면으로 이동
     */
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id, HttpSession session) {

        // 1. 로그인 체크 - Define.SESSION_USER
        User sessionUser = (User)session.getAttribute("sessionUser");

        if (sessionUser == null) {
            // 로그인 페이지로 리다이렉트 처리
            return "redirect:/login-form";
        }

        // 3. 게시물 존재 여부 확인
        Board board = boardRepository.findById(id);

        if (board == null) {
            throw new IllegalArgumentException("이미 삭제된 게시글 입니다.");
        }
        // 4. 소유자 확인 : 권한 체크
        if (!board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        //if (!(sessionUser.getId() == board.getUser().getId())) {
        //    throw new RuntimeException("삭제 권한이 없습니다.");
        //}

        // 5. 권한 확인 이후 삭제 처리
        boardRepository.deleteById(id);

        // 6. 삭제 성공 시 리다이렉트 처리
        return "redirect:/";
    }

    /**
     * 게시글 작성 화면 요청
     * 주소 설계 : http://localhost:8080/board/save-form
     * @param session
     * @return
     */
    @GetMapping("/board/save-form")
    public String saveForm(HttpSession session) {
        // 권한 체크 -> 로그인된 사용자만 이동
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            // 로그인 안한 경우 다시 로그인 페이지로 redirect 처리
            return "redirect:/login-form";
        }

        return "board/save-form";
    }

    // http://localhost:8080/board/save
    // 게시글 저장 액션 처리
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO, HttpSession session) {

        try {

            System.out.println(reqDTO.toString());

            // 1. 권한 체크
            User sessionUser = (User) session.getAttribute("sessionUser");
            if (sessionUser == null) {
                // 로그인 안한 경우 다시 로그인 페이지로 redirect 처리
                return "redirect:/login-form";
            }

            // 2. 유효성 검사
            reqDTO.validate();

            // 3.SaveDTO --> 저장시키기 위해 --> Board 변환을 해 주어야 한다.
            // Board board = reqDTO.toEntity(sessionUser);
            boardRepository.save(reqDTO.toEntity(sessionUser));

            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            // 필요하다면 에러 메세지 내려 줄 수 있음
            return "board/save-form";
        }


    }

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