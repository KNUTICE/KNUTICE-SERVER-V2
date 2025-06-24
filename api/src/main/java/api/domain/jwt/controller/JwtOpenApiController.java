package api.domain.jwt.controller;

import api.domain.jwt.business.JwtTokenBusiness;
import api.domain.jwt.model.JwtTokenDto;
import api.domain.jwt.model.JwtTokenValidationRequest;
import api.domain.jwt.model.JwtTokenValidationResponse;
import global.api.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/auth")
public class JwtOpenApiController {

    private final JwtTokenBusiness jwtTokenBusiness;

    @PostMapping("/token/refresh")
    public Api<JwtTokenDto> reIssueAccessToken(@RequestHeader("Authorization") String refreshToken){
        return Api.OK(jwtTokenBusiness.reIssueAccessToken(refreshToken));
    }

}
