package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public PointHistory create(long userId, long amount, TransactionType type, long updateMillis) {
        log.info("Creating PointHistory for userId: {}, amount: {}, type: {}, timestamp: {}", userId, amount, type, updateMillis);

        PointHistory result = pointHistoryTable.insert(userId, amount, type, updateMillis);
        log.info("PointHistory created successfully with ID: {}", result.id());

        return result;
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        log.info("Fetching PointHistory for userId: {}", userId);

        List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);

        log.info("Found {} PointHistory records for userId: {}", histories.size(), userId);
        return histories;
    }
}
