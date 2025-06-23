package com.tenco.blog.board;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor // 생성자 자동 생성 + 멤버 변수 -> DI 처리 됨
@Repository // IoC + 싱글톤 패턴으로 관리
public class BoardRepository {

    // DI
    private final EntityManager em;

    /**
     * 전체 게시글 조회
     */
    public List<Board> findByAll() {

        // 조회 - JPQL 쿼리 선택
        String jpql = " select b from Board b ORDER BY b.id desc ";

        TypedQuery query = em.createQuery(jpql, Board.class);
        List<Board> boardList = query.getResultList();

        return boardList;
    }

    /**
     * 게시글 단건 조회 (PK 기준)
     * @param id : Board 엔티티의 ID 값
     * @return : Board 엔티티
     */

    public Board findById(Long id) {
        // 조회 - PK 조회는 무조건 EntityManager의 메서드 활용이 이득이다.
        Board board = em.find(Board.class, id);

        return board;
    }
}
