package io.hhplus.tdd.point.unit;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class PointHistoryRepositoryTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void createPointHistory() {
        // given
        pointHistoryTable = new PointHistoryTable();

        // when

        // then
    }
}
