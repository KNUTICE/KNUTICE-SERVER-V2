package api.domain.topic.converter;

import api.domain.topic.controller.model.TopicStatusResponse;
import api.infra.secon.FcmTokenSeconDocument;
import global.annotation.Converter;
import global.utils.NoticeMapper;

@Converter
public class TopicConverter {

    public TopicStatusResponse toResponse(FcmTokenSeconDocument document) {
        return TopicStatusResponse.builder()
            .generalNewsTopic(document.getSubscribedNoticeTopics().contains(NoticeMapper.GENERAL_NEWS.toString()))
            .scholarshipNewsTopic(document.getSubscribedNoticeTopics().contains(NoticeMapper.SCHOLARSHIP_NEWS.toString()))
            .eventNewsTopic(document.getSubscribedNoticeTopics().contains(NoticeMapper.EVENT_NEWS.toString()))
            .academicNewsTopic(document.getSubscribedNoticeTopics().contains(NoticeMapper.ACADEMIC_NEWS.toString()))
            .employmentNewsTopic(document.getSubscribedNoticeTopics().contains(NoticeMapper.EMPLOYMENT_NEWS.toString()))
            .build();
    }

}
