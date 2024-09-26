package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointServiceImpl) {
        this.pointService = Objects.requireNonNull(pointServiceImpl);
    }

    /**
     * 특정 유저의 포인트를 조회하는 기능
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPointDto> point(@PathVariable("id") long id) {

        log.info("GET /point/{} - Retrieving point for user with ID: {}", id, id);
        UserPoint userPoint = pointService.getPoint(UserPointDto.builder()
                .id(id)
                .build());
        log.info("Retrieved point for user ID {}: {}", id, userPoint.getPoint());

        return ResponseEntity.ok(UserPointDto.from(userPoint));
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회 기능
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistoryDto>> history(@PathVariable("id") long userId) {

        log.info("GET /point/{}/histories - Retrieving point histories for user with ID: {}", userId, userId);
        List<PointHistoryDto> histories = pointService.getPointHistory(PointHistoryDto.builder()
                        .userId(userId).build())
                .stream()
                .map(PointHistoryDto::from)
                .collect(Collectors.toList());
        log.info("Retrieved {} point history entries for user ID: {}", histories.size(), userId);

        return ResponseEntity.ok(histories);
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPointDto> charge(@PathVariable("id") long id, @RequestBody long amount) {

        log.info("PATCH /point/{}/charge - Charging {} points for user with ID: {}", id, amount, id);
        UserPoint updatedUserPoint = pointService.chargePoint(UserPointDto.builder()
                .id(id)
                .amount(amount)
                .build());
        log.info("User ID {} now has {} points after charging {} points", id, updatedUserPoint.getPoint(), amount);

        return ResponseEntity.ok(UserPointDto.from(updatedUserPoint));
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<UserPointDto> use(@PathVariable("id") long id, @RequestBody long amount) {

        log.info("PATCH /point/{}/use - Using {} points for user with ID: {}", id, amount, id);
        UserPoint updatedUserPoint = pointService.usePoint(UserPointDto.builder()
                .id(id)
                .amount(amount)
                .build());
        log.info("User ID {} now has {} points after using {} points", id, updatedUserPoint.getPoint(), amount);

        return ResponseEntity.ok(UserPointDto.from(updatedUserPoint));
    }
}
