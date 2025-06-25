package com.tenco.blog.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO {
        private String username;
        private String password;
        private String email;

        // JoinDTO를 User Object로 변환하는 메서드 추가
        // 계층간 데이터 변환을 위해 명확하게 분리
        public User toEntity() {
            return User.builder().username(this.username).password(this.password).email(this.email).build();
        }

        // 회원가입시 유효성 검증 메서드
        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자 명은 필수입니다.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("패스워드는 필수 입니다.");
            }
            // 간단한 이메일 형식 검증 (정규화 표현식)
            if (!email.contains("@")) {
                throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
            }
        }
    }

    // 로그인 용 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String username;
        private String password;

        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("아이디 입력은 필수 입니다.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("패스워드 입력은 필수 입니다.");
            }
        }
    }

}
