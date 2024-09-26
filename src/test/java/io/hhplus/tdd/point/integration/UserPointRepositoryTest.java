package io.hhplus.tdd.point.integration;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserPointRepositoryTest {

    @Autowired
    private UserPointRepository repository;

    private static final long DEFAULT_USER_ID = 1L;
    private static final long POINT = 500L;

    @DisplayName("UserPoint 생성 후 조회")
    @Test
    void createUserPoint() {
        // given
        Optional<UserPoint> userPoint = repository.create(DEFAULT_USER_ID, POINT);

        // when
        Optional<UserPoint> result = repository.selectById(DEFAULT_USER_ID);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(userPoint);
    }
}
