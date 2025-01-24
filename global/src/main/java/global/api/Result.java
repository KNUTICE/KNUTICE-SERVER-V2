package global.api;

import global.api.enums.ResultStatus;
import global.errorcode.ErrorCode;
import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    private Integer resultCode;
    private String resultMessage;
    private String resultDescription;

    public static Result OK(){
        return Result.builder()
            .resultCode(ErrorCode.OK.getErrorCode())
            .resultMessage(ErrorCode.OK.getDescription())
            .resultDescription(ResultStatus.SUCCESS.getDescription())
            .build();
    }


    public static Result ERROR(ErrorCodeIfs errorCodeIfs){
        return Result.builder()
            .resultCode(errorCodeIfs.getErrorCode())
            .resultMessage(errorCodeIfs.getDescription())
            .resultDescription(ResultStatus.FAIL.getDescription())
            .build();
    }

    public static Result ERROR(ErrorCodeIfs errorCodeIfs, Throwable tx){
        return Result.builder()
            .resultCode(errorCodeIfs.getErrorCode())
            .resultMessage(errorCodeIfs.getDescription())
            .resultDescription(tx.getMessage())
            .build();
    }

    public static Result ERROR(ErrorCodeIfs errorCodeIfs, String description){
        return Result.builder()
            .resultCode(errorCodeIfs.getErrorCode())
            .resultMessage(errorCodeIfs.getDescription())
            .resultDescription(description)
            .build();
    }

}
