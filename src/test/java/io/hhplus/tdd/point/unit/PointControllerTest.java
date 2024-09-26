package io.hhplus.tdd.point.unit;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @DisplayName("/point/{id} api 요청 시 UserPointDto를 반환해야한다.")
    @Test
    void getPoint() throws Exception {
        // given
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        when(pointService.getPoint(any(UserPointDto.class))).thenReturn(userPoint);

        // when & then
        mockMvc.perform(get("/point/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.point").value(100L));
    }

    @DisplayName("/point/{id}/charge api 요청 시 UserPointDto를 반환해야한다.")
    @Test
    void chargePoint() throws Exception {
        // given
        when(pointService.chargePoint(any(UserPointDto.class))).thenReturn(new UserPoint(1L, 200L, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/1/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.point").value(200L));
    }

    @DisplayName("/point/{id}/use api 요청 시 UserPointDto를 반환해야한다.")
    @Test
    void usePoint() throws Exception {
        // given
        when(pointService.usePoint(any(UserPointDto.class))).thenReturn(new UserPoint(1L, 50L, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/1/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("50"))  // 사용할 포인트
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.point").value(50L));
    }

    @DisplayName("/point/{id}/histories api 요청 시 List<PointHistoryDto>를 반환해야한다.")
    @Test
    void getPointHistory() throws Exception {
        // given
        when(pointService.getPointHistory(any(PointHistoryDto.class))).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/point/1/histories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
