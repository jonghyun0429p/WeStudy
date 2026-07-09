package com.westudy;

import com.westudy.user.entity.User;
import com.westudy.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class WestudyApplicationTests {

    @Autowired
    private UserMapper userMapper;

	@Test
	void insertUserTest() {
		if (userMapper.findByUsername("apptestuser") != null) {
			userMapper.deleteByUserName("apptestuser");
		}

		User user = User.builder()
				.username("apptestuser")
				.password("password123")
				.nickname("앱테스트유저")
				.email("apptest@example.com")
				.phoneNumber("010-1234-5678")
				.createdAt(LocalDateTime.now())
				.build();

		userMapper.insertUser(user);
		System.out.println("삽입 완료: " + user.getUsername());
	}

}
