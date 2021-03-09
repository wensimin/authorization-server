package tech.shali.authorizationserver.pojo

import tech.shali.authorizationserver.entity.SysAuth
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class AuthVo(
    @get:NotEmpty
    val username: String?,
    @get:NotNull
    val auth: SysAuth?
)