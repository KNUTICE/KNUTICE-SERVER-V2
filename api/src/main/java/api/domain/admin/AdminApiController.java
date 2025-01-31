package api.domain.admin;

import api.common.resolver.AuthUser;
import global.annotation.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    @GetMapping()
    public String test(@AuthenticatedUser AuthUser authUser) {
        return authUser.getUserId();
    }

}
