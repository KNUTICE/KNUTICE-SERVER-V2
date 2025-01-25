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

        // 각 NoticeMapper의 타입에 대해 100개씩 생성
        for (NoticeMapper noticeMapper : NoticeMapper.values()) {
            for (long i = 1; i <= 100; i++) {
                NoticeDocument notice = NoticeDocument.builder()
                    .nttId(i + (100 * noticeMapper.ordinal())) // nttId를 각 카테고리마다 순차적으로 증가하도록 설정
                    .noticeName(noticeMapper)  // NoticeMapper에 해당하는 카테고리명
                    .title(noticeMapper.getCategory() + " Title " + i)
                    .contentNumber((int) i)
                    .contentUrl(noticeMapper.getInternalContentUrl() + "&nttId=" + i)
                    .contentImage("image" + i + ".jpg")
                    .departmentName("Test Department " + i)
                    .registeredAt("2025-01-26")
                    .build();
                notices.add(notice);
            }
        }

        // MongoDB에 400개 데이터 삽입 (4개 카테고리 * 100개)
        mongoTemplate.insertAll(notices);
    }

}
