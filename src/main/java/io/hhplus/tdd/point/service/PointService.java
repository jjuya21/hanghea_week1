package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;

import java.util.List;

public interface PointService {

    public UserPoint join(UserPointDto userPointDto);

    public UserPoint getPoint(UserPointDto userPointDto);

    public List<PointHistory> getPointHistory(PointHistoryDto PointHistoryDto);

    public UserPoint chargePoint(UserPointDto userPointDto);

    public UserPoint usePoint(UserPointDto userPointDto);
}
