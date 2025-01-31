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
@RequestMapping("/open-api/jwt")
public class JwtOpenApiController {

    private final JwtTokenBusiness jwtTokenBusiness;

    @PostMapping("/reissue")
    public Api<JwtTokenDto> reIssueAccessToken(@RequestHeader("Authorization") String refreshToken){
        JwtTokenDto response = jwtTokenBusiness.reIssueAccessToken(refreshToken);
        return Api.OK(response);
    }

    @PostMapping()
    public Api<JwtTokenValidationResponse> validationToken(
        @RequestBody JwtTokenValidationRequest jwtTokenValidationRequest
    ){
        JwtTokenValidationResponse response = jwtTokenBusiness.tokenValidation(
            jwtTokenValidationRequest);
        return Api.OK(response);
    }

}
