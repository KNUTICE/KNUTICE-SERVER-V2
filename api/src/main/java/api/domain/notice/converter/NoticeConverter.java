package api.domain.notice.converter;

import api.domain.notice.controller.model.request.NoticeSaveRequest;
import api.domain.notice.controller.model.request.NoticeUpdateRequest;
import api.domain.notice.controller.model.response.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.request.NoticeRequest;
import api.domain.notice.controller.model.response.NoticeResponse;
import api.domain.notice.controller.model.request.NoticeSearchRequest;
import db.domain.notice.NoticeDocument;
import db.domain.notice.dto.QNoticeDto;
import db.domain.notice.dto.QNoticeSearchDto;
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

    public QNoticeSearchDto toDto(NoticeSearchRequest noticeSearchRequest, Pageable page) {
        return QNoticeSearchDto.builder()
            .keyword(noticeSearchRequest.getKeyword())
            .nttId(noticeSearchRequest.getNttId())
            .page(page)
            .build();
    }

    public List<NoticeResponse> toResponse(List<NoticeDocument> noticeList) {
        return noticeList.stream().map(notice ->
                NoticeResponse.builder()
                    .nttId(notice.getNttId())
                    .title(notice.getTitle())
                    .contentUrl(notice.getContentUrl())
                    .contentImage(notice.getContentImage())
                    .departmentName(notice.getDepartmentName())
                    .registeredAt(notice.getRegisteredAt())
                    .noticeName(notice.getNoticeName())
                    .build())
            .toList();
    }

    public LatestThreeNoticeResponse toResponse(
        List<NoticeDocument> generalNews,
        List<NoticeDocument> scholarshipNews,
        List<NoticeDocument> eventNews,
        List<NoticeDocument> academicNews,
        List<NoticeDocument> employmentNews
    ) {
        return LatestThreeNoticeResponse.builder()
            .latestThreeGeneralNews(toLatestThreeNoticeDto(generalNews))
            .latestThreeScholarshipNews(toLatestThreeNoticeDto(scholarshipNews))
            .latestThreeEventNews(toLatestThreeNoticeDto(eventNews))
            .latestThreeAcademicNews(toLatestThreeNoticeDto(academicNews))
            .latestThreeEmploymentNews(toLatestThreeNoticeDto(employmentNews))
            .build();
    }

    private List<NoticeResponse> toLatestThreeNoticeDto(
        List<NoticeDocument> noticeDocumentsList
    ) {
        return noticeDocumentsList.stream().map(
            noticeDocument -> NoticeResponse.builder()
                .nttId(noticeDocument.getNttId())
                .title(noticeDocument.getTitle())
                .contentUrl(noticeDocument.getContentUrl())
                .contentImage(noticeDocument.getContentImage())
                .departmentName(noticeDocument.getDepartmentName())
                .registeredAt(noticeDocument.getRegisteredAt())
                .noticeName(noticeDocument.getNoticeName())
                .build()
        ).toList();
    }

    public NoticeResponse toResponse(NoticeDocument noticeDocument) {
        return NoticeResponse.builder()
            .nttId(noticeDocument.getNttId())
            .title(noticeDocument.getTitle())
            .contentUrl(noticeDocument.getContentUrl())
            .contentImage(noticeDocument.getContentImage())
            .departmentName(noticeDocument.getDepartmentName())
            .registeredAt(noticeDocument.getRegisteredAt())
            .noticeName(noticeDocument.getNoticeName())
            .build();
    }

    public NoticeDocument toDocument(NoticeSaveRequest noticeSaveRequest) {
        return NoticeDocument.builder()
            .nttId(noticeSaveRequest.getNttId())
            .noticeName(noticeSaveRequest.getNoticeName())
            .title(noticeSaveRequest.getTitle())
            .contentNumber(noticeSaveRequest.getContentNumber())
            .contentUrl(noticeSaveRequest.getContentUrl())
            .contentImage(noticeSaveRequest.getContentImage())
            .departmentName(noticeSaveRequest.getDepartmentName())
            .registeredAt(noticeSaveRequest.getRegisteredAt())
            .build();
    }

    public void updateDocument(NoticeDocument noticeDocument, NoticeUpdateRequest request) {
        noticeDocument.setContentNumber(request.getContentNumber());
        noticeDocument.setTitle(request.getTitle());
        noticeDocument.setContentUrl(request.getContentUrl());
        noticeDocument.setContentImage(request.getContentImage());
        noticeDocument.setDepartmentName(request.getDepartmentName());
        noticeDocument.setRegisteredAt(request.getRegisteredAt());
        noticeDocument.setNoticeName(request.getNoticeName());
    }

}
