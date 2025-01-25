package api.domain.notice.converter;

import api.domain.notice.controller.model.noticelist.NoticeRequest;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import db.domain.notice.dto.QNoticeDto;
import global.annotation.Converter;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Converter
public class NoticeConverter {

    public QNoticeDto toDto(NoticeRequest noticeRequest, Pageable page) {
        return QNoticeDto.builder()
            .noticeName(noticeRequest.getNoticeName())
            .nttId(noticeRequest.getNttId())
            .page(page)
            .build();
    }

    public List<NoticeResponse> toResponse(List<NoticeProjection> noticeList) {
        return noticeList.stream().map(notice ->
                NoticeResponse.builder()
                    .nttId(notice.getNttId())
                    .contentNumber(notice.getContentNumber())
                    .title(notice.getTitle())
                    .contentUrl(notice.getContentUrl())
                    .contentImage(notice.getContentImage())
                    .departName(notice.getDepartName())
                    .registeredAt(notice.getRegisteredAt())
                    .build())
            .toList();
    }

}
