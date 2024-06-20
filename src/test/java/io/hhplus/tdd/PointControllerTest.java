package io.hhplus.tdd;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    /**
     given : 특정 조건 설정
     when : 실제 동작 수행
     then : 결과 검증
     **/

    @Test
    @DisplayName("특정 유저의 포인트를 조회하는 테스트")
    public void getUserPointTest() throws Exception {

        // given
        long userId = 1L;
        long point = 1000L;

        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        given(pointService.getUserPoint(userId)).willReturn(userPoint);                 // return 값으로 userPoint를 주입

        // when & then
        mockMvc.perform(get("/point/{id}", userId)          // 호출시 willReturn(userPoint) 객체를 반환
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))                // userPoint 값과 일치하는지 응답값을 검증
                .andExpect(jsonPath("$.point").value(point))
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(print())                                                     // 상세 내용 확인할 수 있도록 콘솔에 출력
                .andReturn();                                                       // 결과를 반환

    }

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회하는 테스트")
    public void getUserHistoryTest() throws Exception {

        // given
        long id1 = 100L;
        long id2 = 101L;
        long userId = 1L;
        long amount1 = 1000L;
        long amount2 = 500L;

        PointHistory pointHistory1 = new PointHistory(id1, userId, amount1, CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = new PointHistory(id2, userId, amount2, USE, System.currentTimeMillis());
        given(pointService.selectAllByUserId(userId)).willReturn(Arrays.asList(pointHistory1, pointHistory2));

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId)             // 호출시 willReturn(Arrays.asList(pointHistory1, pointHistory2)) 객체를 반환
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1))                          // [0]이 pointHistory1 과 일치하는지 응답값을 검증
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].amount").value(amount1))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[0].updateMillis").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(id2))                          // [1]이 pointHistory2 와 일치하는지 응답값을 검증
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[1].amount").value(amount2))
                .andExpect(jsonPath("$[1].type").value("USE"))
                .andExpect(jsonPath("$[1].updateMillis").isNotEmpty())
                .andDo(print())                                                               // 상세 내용 확인할 수 있도록 콘솔에 출력
                .andReturn();                                                                 // 결과를 반환

    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 테스트")
    public void chargeUserPointTest() throws Exception {

        // given
        long userId = 1L;
        long originalPoint = 1000L;
        long amount = 500L;

        UserPoint chargeUserPoint = new UserPoint(userId, originalPoint + amount, System.currentTimeMillis());
        given(pointService.chargeUserPoint(userId, amount)).willReturn(chargeUserPoint);                    // return 값으로 chargeUserPoint 주입

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)                                    // 호출시 willReturn(chargeUserPoint) 객체를 반환
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))                                                   // amount 전달
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))                                      // chargeUserPoint 값과 일치하는지 응답값을 검증
                .andExpect(jsonPath("$.point").value(originalPoint + amount))   // 충전 후 포인트 검증
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(print())                                                                             // 상세 내용 확인할 수 있도록 콘솔에 출력
                .andReturn();                                                                               // 결과를 반환

    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 테스트")
    public void useUserPointTest() throws Exception {

        // given
        long userId = 1L;
        long originalPoint = 1000L;
        long amount = 500L;

        UserPoint useUserPoint = new UserPoint(userId, originalPoint - amount, System.currentTimeMillis());
        given(pointService.useUserPoint(userId, amount)).willReturn(useUserPoint);                         // return 값으로 useUserPoint 주입

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)                                        // 호출시 willReturn(useUserPoint) 객체를 반환
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))                                                   // amount 전달
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))                                      // useUserPoint 값과 일치하는지 응답값을 검증
                .andExpect(jsonPath("$.point").value(originalPoint - amount))   // 사용 후 포인트 검증
                .andExpect(jsonPath("$.updateMillis").isNotEmpty())
                .andDo(print())                                                                             // 상세 내용 확인할 수 있도록 콘솔에 출력
                .andReturn();                                                                               // 결과를 반환

    }

}
