package api.common.exception;
import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmException;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// WebMvcTest에 GlobalExceptionHandler와 테스트용 컨트롤러를 함께 로드
@WebMvcTest(controllers = {GlobalExceptionHandlerTest.FcmTokenTestController.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;


    // 테스트용 컨트롤러를 하나 만들고
    // 테스트용 컨트롤러에서는, 예외를 발생시킬것.
    // 그럼 해당 예외를 캐치해야함.
    @RestController
    @RequestMapping("/test")
    public static class FcmTokenTestController {
        @GetMapping("/FcmTokenNotFoundException")
        public String fcmTokenNotFoundException() {
            throw new FcmException.FcmTokenNotFoundException(FcmTokenErrorCode.TOKEN_NOT_FOUND);
        }
    }


    // BaseException 발생 시 전역 예외 핸들러가 올바른 HTTP 상태 코드와 응답 구조를 반환하는지 검증합니다.
    @Test
    public void fcmTokenNotFoundException_원인_예외_출력() throws Exception {
        mockMvc.perform(get("/test/FcmTokenNotFoundException")).andExpect(status().is(FcmTokenErrorCode.TOKEN_NOT_FOUND.getHttpCode()));
    }
}
