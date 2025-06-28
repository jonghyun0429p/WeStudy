package com.westudy;

import com.westudy.user.entity.User;
import com.westudy.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class WestudyApplicationTests {

    @Autowired
    private UserMapper userMapper;

	@Test
	void insertUserTest() {
		User user = User.builder()
				.username("testuser")
				.password("password123")
				.nickname("테스트유저")
				.email("test@example.com")
				.phoneNumber("010-1234-5678")
				.createdAt(LocalDateTime.now())
				.build();

		userMapper.insertUser(user);
		System.out.println("삽입 완료: " + user.getUsername());
	}

}
