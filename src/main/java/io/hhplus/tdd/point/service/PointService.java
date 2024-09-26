package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;

import java.util.List;

public interface PointService {

    public UserPointDto join(long id, long point);

    public UserPointDto getPoint(long id);

    public List<PointHistoryDto> getPointHistory(long id);

    public UserPointDto chargePoint(long id, long amount);

    public UserPointDto usePoint(long id, long amount);
}
