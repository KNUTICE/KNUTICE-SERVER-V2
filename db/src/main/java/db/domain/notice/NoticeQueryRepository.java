package db.domain.notice;

import com.querydsl.core.BooleanBuilder;
import db.domain.notice.dto.QNoticeDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeQueryRepository extends QuerydslRepositorySupport {

    public NoticeQueryRepository(@Qualifier("mongoTemplate") MongoOperations operations) {
        super(operations);
    }

    public List<NoticeDocument> findNoticeBy(QNoticeDto qNoticeDto) {

        // 동적 쿼리 조건 생성
        BooleanBuilder builder = buildQueryConditions(qNoticeDto);

        // MorphiaQuery를 사용하여 쿼리 실행
        MorphiaQuery<Notice> query = new MorphiaQuery<>(mongoOperations, QNotice.notice);
        query.where(builder);

        // 결과를 Projection 형식으로 변환하여 반환
        return query.select(Projections.constructor(NoticeProjection.class,
            QNotice.notice.id,
            QNotice.notice.title,
            QNotice.notice.content,
            QNotice.notice.registeredAt
        )).fetch();
    }

    private BooleanBuilder buildQueryConditions(QNoticeDto qNoticeDto) {
        BooleanBuilder builder = new BooleanBuilder();

        if (qNoticeDto.getId() != null) {
            builder.and(QNotice.notice.id.eq(qNoticeDto.getId()));
        }

        if (qNoticeDto.getTitle() != null) {
            builder.and(QNotice.notice.title.containsIgnoreCase(qNoticeDto.getTitle()));
        }

        if (qNoticeDto.getRegisteredAt() != null) {
            builder.and(QNotice.notice.registeredAt.eq(qNoticeDto.getRegisteredAt()));
        }

        return builder;
    }
}