package com.tenco.blog.board;

import com.tenco.blog.user.User;
import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// 기본 생성자 - JPA에서 엔티티는 기본 생성자가 필요
@NoArgsConstructor
@Data
@Table(name = "board_tb")
@Entity
public class Board {

    @Id
    // IDENTITY 전략 : 데이터베이스의 기본 전략을 사용한다. -> AUTO_INCREMENT (MySQL 방식)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 별도 어노테이션이 없으면 필드명이 컬럼명이 됨
    private String title;
    private String content;

    // v2에서 사용했던 방식
    //private String username;
    // v3에서 Board 엔티티는 User 엔티티와 연관관계가 성립이 됨

    // 다대일
    // 여러개의 게시글에는 한명의 작성자를 가질 수 있다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키 컬럼명 명시
    private User user;

    @CreationTimestamp
    private Timestamp createdAt; // created_at(스네이크 케이스로 자동변환)



    // 생성자 만들어 주기
//    public Board(String title, String content, String username) {
//        this.title = title;
//        this.content = content;
//        //this.username = username;
//        // id와 createdAt은 JPA/Hibernate 가 자동으로 설정
//    }

    // 머스태치에서 표현할 시간을 포맷기능을(행위) 스르로 만들자
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }
}
