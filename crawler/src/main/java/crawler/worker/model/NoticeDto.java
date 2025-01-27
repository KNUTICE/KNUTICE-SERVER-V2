package crawler.worker.model;

import global.utils.NoticeMapper;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDto {
    private NoticeMapper noticeMapper;
    private Long nttId;
    private Integer contentNumber;
    private String title;
    private String contentUrl;
    private String contentImage;
    private String departName;
    private String registeredAt;
    private boolean duplication = false;

    @Override
    public boolean equals(Object targetObjDto) {
        if (this == targetObjDto) return true;
        if (targetObjDto == null || getClass() != targetObjDto.getClass()) return false;
        NoticeDto targetDto = (NoticeDto) targetObjDto;

        // 서로 nttId 가 같다면, 중복 게시글이므로 둘 중 하나를 삭제 후, 중복 필드 업데이트
        // 둘 중 어떤게 삭제 될지 모르기 때문에, 둘다 true 로 변경
        if(Objects.equals(nttId, targetDto.nttId)) {
            this.duplication = true;
            targetDto.duplication = true;
            return true;
        }

        // nttId 가 서로 다르면, 다른 객체 이므로 false 를 반환
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nttId);
    }
}
