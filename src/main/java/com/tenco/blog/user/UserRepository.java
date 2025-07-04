package com.tenco.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;

@RequiredArgsConstructor // 생성자 의존 주입 - DI 처리
@Repository
public class UserRepository {

    // 생성자 의존 주입 (DI)
    private final EntityManager em;

    /**
     * 로그인 요청 기능 (사용자 정보 조회)
     * @param username
     * @param password
     * @return 성공 시 User 엔티티 실패 시 null 반환
     */
    public User findByUsernameAndPassword(String username, String password) {

        try {
            String jpql = " select u from User u where u.username = :username and u.password = :password ";
            TypedQuery typedQuery = em.createQuery(jpql,User.class);

            typedQuery.setParameter("username", username);
            typedQuery.setParameter("password", password);
            return (User)typedQuery.getSingleResult();
        } catch (Exception e) {
            // 일치하는 사용자가 없거나 에러 발생 시 null 반환
            // 즉, 로그인 실패를 의미함.
            return null;
        }
    }

    /**
     * 회원정보 저장
     * @param user (비영속 상태)
     * @return User 엔티티 반환
     */
    @Transactional
    public User save(User user) {
        // 매개변수에 들어오는 user object는 비영속화 된 상태이다.
        em.persist(user); // 영속성 컨텍스트에 user 객체를 관리하기 시작 함
        // 트랜잭션 커밋 시점에 실제 DB INSERT 쿼리를 실행한다.
        return user;
    }

    // 사용자명 중복 체크용 조회 기능
    public User findByUsername(String username) {
        // where username = ?
        // select ->
        // String jpql = " SELECT u From User u WHERE u.username = :username ";
        // TypedQuery<User> typedQuery = em.createQuery(jpql, User.class);
        // typedQuery.setParameter("username", username);

        try {
            String jpql = " SELECT u From User u WHERE u.username = :username ";
            return em.createQuery(jpql, User.class).setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

}
