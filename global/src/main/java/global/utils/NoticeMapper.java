package global.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeMapper {

    GENERAL_NEWS(
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000059/selectBoardList.do",
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000059/selectBoardArticle.do?bbsId=BBSMSTR_000000000059",
        "일반소식"
    ),

    SCHOLARSHIP_NEWS(
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000060/selectBoardList.do",
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000060/selectBoardArticle.do?bbsId=BBSMSTR_000000000060",
        "장학안내"
    ),

    EVENT_NEWS(
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000061/selectBoardList.do",
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000061/selectBoardArticle.do?bbsId=BBSMSTR_000000000061",
        "행사안내"
    ),

    ACADEMIC_NEWS(
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000055/selectBoardList.do",
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000055/selectBoardArticle.do?bbsId=BBSMSTR_000000000055",
        "학사공지사항"
    ),

    EMPLOYMENT_NEWS(
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000179/selectBoardList.do",
        "https://www.ut.ac.kr/cop/bbs/BBSMSTR_000000000179/selectBoardArticle.do?bbsId=BBSMSTR_000000000179",
        "취업지원안내"
    )

    ;

    private final String noticeUrl;
    private final String internalContentUrl;
    private final String category;

}
