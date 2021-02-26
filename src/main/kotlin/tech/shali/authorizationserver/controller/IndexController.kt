package tech.shali.authorizationserver.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
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
}