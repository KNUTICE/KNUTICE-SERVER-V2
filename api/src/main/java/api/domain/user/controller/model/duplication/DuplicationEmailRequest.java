package api.domain.user.controller.model.duplication;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicationEmailRequest {

    @Pattern(
        regexp = "^[0-9a-zA-Z]{1,50}@[0-9a-zA-Z]{1,24}+(\\.[0-9a-zA-Z]+){1,24}$",
        message = "올바른 이메일 형식을 입력하세요 (예: example@domain.com)"
    )
    private String email;

}
