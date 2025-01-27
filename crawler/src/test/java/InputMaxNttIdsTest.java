import crawler.CrawlerApplication;
import db.domain.notice.MaxNoticeIdsDocument;
import db.domain.notice.MaxNoticeIdsMongoRepository;
import global.utils.NoticeMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CrawlerApplication.class)
public class InputMaxNttIdsTest {

    @Autowired
    private MaxNoticeIdsMongoRepository maxNoticeIdsMongoRepository;

    @Test
    void inputMaxNttIds(){
        // 예시 코드로 MongoDB에 저장하는 부분
        MaxNoticeIdsDocument document1 = MaxNoticeIdsDocument.builder()
            .noticeMapper(NoticeMapper.GENERAL_NEWS)
            .announcementMaxNttId(1076760L)
            .announcementRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .noticeMaxNttId(1076760L)
            .noticeRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .build();

        MaxNoticeIdsDocument document2 = MaxNoticeIdsDocument.builder()
            .noticeMapper(NoticeMapper.SCHOLARSHIP_NEWS)
            .announcementMaxNttId(1076455L)
            .announcementRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .noticeMaxNttId(1076476L)
            .noticeRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .build();

        MaxNoticeIdsDocument document3 = MaxNoticeIdsDocument.builder()
            .noticeMapper(NoticeMapper.EVENT_NEWS)
            .announcementMaxNttId(1075220L)
            .announcementRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .noticeMaxNttId(1076397L)
            .noticeRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .build();

        MaxNoticeIdsDocument document4 = MaxNoticeIdsDocument.builder()
            .noticeMapper(NoticeMapper.ACADEMIC_NEWS)
            .announcementMaxNttId(1076738L)
            .announcementRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .noticeMaxNttId(1076718L)
            .noticeRegisteredAt(LocalDateTime.parse("2024-09-23T14:14:21"))
            .build();


        maxNoticeIdsMongoRepository.saveAll(Arrays.asList(document1, document2, document3, document4));
    }
}
