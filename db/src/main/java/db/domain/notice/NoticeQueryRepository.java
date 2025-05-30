package db.domain.notice;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import db.domain.notice.dto.QNoticeDto;
import db.domain.notice.dto.QNoticeSearchDto;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static db.domain.notice.QNoticeDocument.*;

@Repository
public class NoticeQueryRepository extends QuerydslRepositorySupport {

    public NoticeQueryRepository(@Qualifier("mongoTemplate") MongoOperations operations) {
        super(operations);
    }

    public List<NoticeDocument> findNoticeBy(QNoticeDto qNoticeDto) {
        return from(noticeDocument)
            .where(
                noticeDocument.noticeName.eq(qNoticeDto.getNoticeName()),
                ltNttId(qNoticeDto.getNttId())
            )
            .orderBy(getOrderSpecifier(qNoticeDto.getPage().getSort()).stream()
                .toArray(size -> new OrderSpecifier[size]))
            .limit(qNoticeDto.getPage().getPageSize())
            .fetch();
    }

    public List<NoticeDocument> findLatestThreeNoticeBy(NoticeMapper noticeName) {
        return from(noticeDocument)
            .where(noticeDocument.noticeName.eq(noticeName))
            .orderBy(noticeDocument.nttId.desc())
            .limit(3)
            .fetch();
    }

    public List<NoticeDocument> findSearchBy(QNoticeSearchDto qNoticeSearchDto) {
        return from(noticeDocument)
            .where(
                containTitle(qNoticeSearchDto.getKeyword()),
                ltNttId(qNoticeSearchDto.getNttId())
            )
            .orderBy(getOrderSpecifier(qNoticeSearchDto.getPage().getSort()).stream()
                .toArray(size -> new OrderSpecifier[size]))
            .limit(qNoticeSearchDto.getPage().getPageSize())
            .fetch();
    }



    private BooleanExpression containTitle(String keyword) {
        return keyword != null ? noticeDocument.title.contains(keyword) : null;
    }

    private Predicate ltNttId(Long nttId) {
        return nttId != null ? noticeDocument.nttId.lt(nttId) : null;
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        sort.stream().forEach(order -> {
            PathBuilder orderByExpression = new PathBuilder(NoticeDocument.class,
                "noticeDocument");
            orders.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC,
                orderByExpression.get(order.getProperty())));

        });
        return orders;
    }

}