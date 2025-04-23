package api.domain.jwt.ifs;

import api.domain.jwt.model.JwtTokenDto;
import java.util.Map;

public interface JwtTokenHelperIfs {

    JwtTokenDto issueAccessToken(Map<String, Object> data);
    JwtTokenDto issueRefresh(Map<String, Object> data);
    Map<String, Object> validationTokenWithThrow(String token);

}
