package com.tenco.blog.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(UserRepository.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired // DI 처리
    private UserRepository userRepository;

    @Test
    public void findByUsernameAndPassword_로그인_성공_테스트() {
        // given
        String username = "ssar";
        String password = "1234";

        // when
        User user = userRepository.findByUsernameAndPassword(username,password);

        // then
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getUsername()).isEqualTo("ssar");
    }

    @Test
    public void save_회원가입_테스트() {
        // given : 회원가입 시 사용할 사용자 정보
        User user = User.builder().username("testUser").email("a@naver.com").password("asd1234").build();

        // when
        User savedUser = userRepository.save(user);

        // then
        // id 할당 여부 확인
        Assertions.assertThat(savedUser.getId()).isNotNull();
        // 데이터가 정상 등록 되었는지 확인
        Assertions.assertThat(savedUser.getUsername()).isEqualTo("testUser");

        // 원본 user Object와 영속화된 Object가 동일한 객체인지(참조) 확인
        // 영속성 컨텍스트는 같은 엔티티에 대해 같은 인스턴스를 보장
        Assertions.assertThat(user).isSameAs(savedUser);
    }

    @Test
    public void save_findByUsername_test() {
        // given
        String username = "홍길동";

        // when
        User findUser = userRepository.findByUsername(username);

        // then
        Assertions.assertThat(findUser).isNotNull();
    }

    @Test
    public void findByUsername_존재하지_않는_사용자_테스트() {
        // given
        String username = "nonUser";

        // when
        User nonUser = userRepository.findByUsername(username);

        // then
        Assertions.assertThat(nonUser).isNull();
    }
}
