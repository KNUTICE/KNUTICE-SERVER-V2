//package crawler.service;
//
//import crawler.fcmutils.FcmUtils;
//import crawler.service.model.FcmDto;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class FcmServiceTest {
//
//    @Autowired
//    private FcmService fcmService;
//
//    @Autowired
//    private FcmUtils fcmUtils;
//
//    @Test
//    void 메시지_전송() {
//
//        FcmDto fcmDto = FcmDto.builder()
//            .title("테스트 메시지")
//            .content("테스트 내용")
//            .build();
//
//
//        List<String> testTokenList = List.of(
//            "INSERT_MY_TOKEN",
//            "testToken1","testToken2"
//        );
//
//
//        fcmService.batchSend(fcmDto, testTokenList);
//
//
//
//    }
//
//}
