package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDto {

    long id;
    long userId;
    long amount;
    TransactionType type;
    long updateMilli;

    public static PointHistoryDto from(PointHistory pointHistory) {

        if (pointHistory == null) return null;

        return PointHistoryDto.builder()
                .id(pointHistory.id())
                .userId(pointHistory.userId())
                .amount(pointHistory.amount())
                .type(pointHistory.type())
                .updateMilli(pointHistory.updateMillis())
                .build();
    }
}
