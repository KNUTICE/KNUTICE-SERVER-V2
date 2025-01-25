package global.api;

import global.errorcode.ErrorCodeIfs;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@Builder
@RequiredArgsConstructor
public class Api<T> {

    private final Result result;

    @Valid
    private final T body;

    /**
     * 본 제네릭 메소드는 <b style="color:orange">'정상응답'</b>을 반환하는 메소드입니다.<br>
     * @param data 상한 또는 하한이 없어 Object 타입도 받을 수 있습니다.<br>
     * @author itstime0809
     */
    public static <G> Api<G> OK(G data){
        return Api.<G>builder()
                .body(data)
                .result(Result.OK())
                .build();
    }

    /**
     * 본 제네릭 메소드는 <b style="color:orange">'비정상응답'</b>을 반환하는 메소드입니다.<br>
     * @param  errorCodeIfs ErrorCodeIfs 를 상한으로 제한하고 본 인터페이스를 구현한 구현체 클래스는 모두 파라미터로 받을 수 있습니다. </br>
     * @author itstime0809
     */
    public static <G extends ErrorCodeIfs> Api<G> ERROR(G errorCodeIfs){

        return Api.<G>builder()
                .result(Result.ERROR(errorCodeIfs))
                .build();
    }


    /**
     * 본 제네릭 메소드는 <b style="color:orange">'비정상응답'</b>을 반환하는 메소드입니다.<br>
     * 오버로딩된 메소드이며 String 타입을 파라미터로 받습니다.
     * @param description 응답에 대한 설명을 추가할 수 있습니다.
     * @author itstime0809
     */
    public static <G extends ErrorCodeIfs> Api<G> ERROR(G errorCodeIfs, String description){
        return Api.<G>builder()
                .result(Result.ERROR(errorCodeIfs, description))
                .build();
    }

}

