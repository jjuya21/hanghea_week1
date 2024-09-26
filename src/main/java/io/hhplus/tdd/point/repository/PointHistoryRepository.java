package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;

import java.util.List;
import java.util.Optional;

public interface PointHistoryRepository {

    public Optional<PointHistory> create(long userId, long amount, TransactionType type, long updateMillis);

    public List<PointHistory> selectAllByUserId(long userId);
}
