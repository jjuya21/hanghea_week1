package io.hhplus.tdd.point.integration;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository repository;

    private static final long DEFAULT_USER_ID = 3L;
    private static final long POINT = 500L;

    @DisplayName("PointHistory 생성 후 조회 시 충전/사용 내역을 포함해서 반환해야한다.")
    @Test
    void createPointHistory() {
        // given
        TransactionType type = TransactionType.CHARGE;

        PointHistory pointHistory = repository.create(DEFAULT_USER_ID, POINT, type, System.currentTimeMillis());

        // when
        List<PointHistory> histories = repository.selectAllByUserId(DEFAULT_USER_ID);

        // then
        assertThat(histories).isNotEmpty();
        assertThat(histories).contains(pointHistory);
    }

    @DisplayName("PointHistory 여러개 생성 후 조회 시 충전/사용 내역을 포함해서 반환해야한다.")
    @Test
    void retrieveMultiplePointHistories() {
        // given
        TransactionType type_1 = TransactionType.CHARGE;
        TransactionType type_2 = TransactionType.USE;

        PointHistory pointHistory_1 = repository.create(DEFAULT_USER_ID, POINT, type_1, System.currentTimeMillis());
        PointHistory pointHistory_2 = repository.create(DEFAULT_USER_ID, POINT, type_2, System.currentTimeMillis());

        // when
        List<PointHistory> histories = repository.selectAllByUserId(DEFAULT_USER_ID);

        // then
        assertThat(histories).hasSize(2);
        assertThat(histories).contains(pointHistory_1);
        assertThat(histories).contains(pointHistory_2);
    }
}
