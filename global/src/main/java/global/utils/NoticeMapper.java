package global.utils;

import global.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeMapper {

    GENERAL_NEWS("generalNews", (byte) 0,
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000059/selectBoardList.do",
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000059/selectBoardArticle.do?bbsId=BBSMSTR_000000000059",
            "일반소식"),
    SCHOLARSHIP_NEWS("scholarshipNews", (byte) 1,
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000060/selectBoardList.do",
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000060/selectBoardArticle.do?bbsId=BBSMSTR_000000000060",
            "장학안내"),
    EVENT_NEWS("eventNews", (byte) 2,
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000061/selectBoardList.do",
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000061/selectBoardArticle.do?bbsId=BBSMSTR_000000000061",
            "행사안내"),
    ACADEMIC_NEWS("academicNews", (byte) 3,
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000055/selectBoardList.do",
            "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000055/selectBoardArticle.do?bbsId=BBSMSTR_000000000055",
            "학사공지사항");

    private final String noticeName;
    private final byte noticeType;
    private final String noticeUrl;
    private final String internalContentUrl;
    private final String category;

    public static byte getNoticeTypeBy(String noticeName) {
        return switch (noticeName) {
            case "generalNews" -> GENERAL_NEWS.getNoticeType();
            case "scholarshipNews" -> SCHOLARSHIP_NEWS.getNoticeType();
            case "eventNews" -> EVENT_NEWS.getNoticeType();
            case "academicNews" -> ACADEMIC_NEWS.getNoticeType();
            default -> throw new RuntimeException(ErrorCode.BAD_REQUEST.getDescription());
        };
    }

    public static String getCategoryBy(byte noticeType) {
        for (NoticeMapper notice : NoticeMapper.values()) {
            if (notice.getNoticeType() == noticeType) {
                return notice.getCategory();
            }
        }
        throw new RuntimeException(ErrorCode.BAD_REQUEST.getDescription());
    }

    public static String getNoticeNameBy(byte noticeType) {
        for (NoticeMapper notice : NoticeMapper.values()) {
            if (notice.getNoticeType() == noticeType) {
                return notice.getNoticeName();
            }
        }
        throw new RuntimeException(ErrorCode.BAD_REQUEST.getDescription());
    }
}
