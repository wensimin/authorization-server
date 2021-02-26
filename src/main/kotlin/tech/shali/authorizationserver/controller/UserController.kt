package tech.shali.authorizationserver.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import tech.shali.authorizationserver.entity.SysUser
import tech.shali.authorizationserver.pojo.RegisterVo
import tech.shali.authorizationserver.service.SysUserService
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(private val sysUserService: SysUserService) {

    @GetMapping
    fun info(principal: Principal): SysUser {
        return sysUserService.info(principal)
    }

    @PostMapping
    fun register(@RequestBody @Valid registerVo: RegisterVo): SysUser {
        return sysUserService.register(registerVo)
    }
}