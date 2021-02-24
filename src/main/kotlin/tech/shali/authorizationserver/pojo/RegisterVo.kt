package tech.shali.authorizationserver.pojo

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class RegisterVo(
    @NotEmpty @get:Length(min = 5, max = 20) var username: String?,
    @NotEmpty @get:Length(min = 8, max = 20) var password: String?
)