package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public PointService(PointHistoryTable pointHistoryTable, UserPointTable userPointTable) {

        this.pointHistoryTable = Objects.requireNonNull(pointHistoryTable);
        this.userPointTable = Objects.requireNonNull(userPointTable);
    }

    public UserPoint getPoint(Long id) {

        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistory(Long id) {

        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint chargePoint(Long id, long amount) {

        UserPoint userPoint = getPoint(id);
        long now = System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000;

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, now - oneDayInMillis);

        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
    }

    public UserPoint usePoint(Long id, long amount) {

        UserPoint userPoint = getPoint(id);
        long now = System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000;

        pointHistoryTable.insert(id, amount, TransactionType.USE, now - oneDayInMillis);

        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() - amount);
    }
}
