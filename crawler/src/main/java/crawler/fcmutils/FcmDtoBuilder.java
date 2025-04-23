package crawler.fcmutils;

import crawler.service.model.FcmDto;
import crawler.worker.model.NoticeDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmDtoBuilder {

    public FcmDto createMultipleFcmMessage(List<NoticeDto> noticeDtoList) {
        return FcmDto.builder()
            .nttId(noticeDtoList.get(0).getNttId())
            .title(noticeDtoList.get(0).getNoticeMapper().getCategory())
            .content(noticeDtoList.get(noticeDtoList.size() - 1).getTitle()
                + "...외 "
                + (noticeDtoList.size() - 1)
                + "개의 공지가 작성되었어요!")
            .noticeName(noticeDtoList.get(0).getNoticeMapper())
            .contentUrl(noticeDtoList.get(0).getContentUrl())
            .contentImage(noticeDtoList.get(0).getContentImage())
            .departmentName(noticeDtoList.get(0).getDepartName())
            .registeredAt(noticeDtoList.get(0).getRegisteredAt())
            .build();
    }

    public FcmDto createSingleFcmMessage(NoticeDto noticeDto) {
        return FcmDto.builder()
            .nttId(noticeDto.getNttId())
            .title(noticeDto.getNoticeMapper().getCategory())
            .content(noticeDto.getTitle())
            .noticeName(noticeDto.getNoticeMapper())
            .contentUrl(noticeDto.getContentUrl())
            .contentImage(noticeDto.getContentImage())
            .departmentName(noticeDto.getDepartName())
            .registeredAt(noticeDto.getRegisteredAt())
            .build();
    }

}
