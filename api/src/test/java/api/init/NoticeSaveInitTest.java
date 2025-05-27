package api.init;

import static org.assertj.core.api.Assertions.assertThat;

import db.domain.notice.NoticeDocument;
import db.domain.notice.NoticeMongoRepository;
import global.utils.NoticeMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoticeSaveInitTest {

    @Autowired
    private NoticeMongoRepository noticeMongoRepository;

    public void initNotice() {
        List<NoticeDocument> notices = new ArrayList<>();

        long nttIdCounter = 1L; // nttId를 1부터 시작하도록 설정

        for (NoticeMapper type : NoticeMapper.values()) {
            for (int i = 1; i <= 10; i++) {
                notices.add(NoticeDocument.builder()
                    .nttId(nttIdCounter++)  // 순차적으로 nttId 할당
                    .noticeName(type)
                    .title(type.name() + " 테스트 title " + i)
                    .contentNumber(i)
                    .contentUrl("https://test.com/content/" + i)
                    .contentImage("https://test.com/image/" + i + ".jpg")
                    .departmentName("테스트 departmentName " + i)
                    .registeredAt(LocalDate.now().toString())
                    .build());
            }
        }

        // 데이터 삽입
        noticeMongoRepository.saveAll(notices);

    }

}