package tech.shali.authorizationserver.pojo

import org.hibernate.validator.constraints.Length

data class RegisterVo(
    @get:Length(min = 5, max = 20) var username: String?,
    @get:Length(min = 8, max = 20) var password: String?
)