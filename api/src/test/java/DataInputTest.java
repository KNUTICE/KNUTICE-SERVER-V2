import api.ApiApplication;
import db.domain.notice.NoticeDocument;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest(classes = ApiApplication.class)
class DataInputTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void insertTestData() {
        List<NoticeDocument> notices = new ArrayList<>();

        for (long i = 1; i <= 100; i++) {
            NoticeDocument notice = NoticeDocument.builder()
                .nttId(i)
                .noticeName(NoticeMapper.GENERAL_NEWS)  // GENERAL_NEWS로 설정
                .title("Test Title " + i)
                .contentNumber((int) i)
                .contentUrl("https://www.ut.ac.kr/test-url-" + i)
                .contentImage("image" + i + ".jpg")
                .departmentName("Test Department " + i)
                .registeredAt("2025-01-26")
                .build();
            notices.add(notice);
        }

        // MongoDB에 100개 데이터 삽입
        mongoTemplate.insertAll(notices);
    }

}