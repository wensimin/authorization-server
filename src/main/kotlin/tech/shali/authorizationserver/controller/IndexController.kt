package tech.shali.authorizationserver.controller

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal
import javax.servlet.http.HttpServletRequest


@Controller
class IndexController {
    @GetMapping("login")
    fun login(request: HttpServletRequest): String {
        return "login"
    }

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping(value = ["/oauth2/consent"])
    fun consent(
        principal: Principal, model: Model,
        @RequestParam(OAuth2ParameterNames.CLIENT_ID) clientId: String?,
        @RequestParam(OAuth2ParameterNames.SCOPE) scope: String?,
        @RequestParam(OAuth2ParameterNames.STATE) state: String?
    ): String {
        model.addAttribute("clientId", clientId)
        model.addAttribute("state", state)
        model.addAttribute("scopes", StringUtils.delimitedListToStringArray(scope, " "))
        model.addAttribute("principalName", principal.name)
        return "consent"
    }


}