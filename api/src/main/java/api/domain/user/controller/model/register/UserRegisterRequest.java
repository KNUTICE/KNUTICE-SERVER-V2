package api.domain.user.controller.model.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @Pattern(
        regexp = "^[0-9a-zA-Z]{1,50}@[0-9a-zA-Z]{1,24}+(\\.[0-9a-zA-Z]+){1,24}$",
        message = "올바른 이메일 형식을 입력하세요 (예: example@domain.com)"
    )
    @NotBlank
    private String email;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,100}$",
        message = "대문자, 소문자, 숫자를 포함하고 8자 이상이어야 합니다."
    )
    @NotBlank(message = "필수 입력 사항입니다.")
    private String password;

    @Size(min = 1, max = 50, message = "이름은 1자 이상, 50자 이하로 입력해주세요.")
    private String name;

}