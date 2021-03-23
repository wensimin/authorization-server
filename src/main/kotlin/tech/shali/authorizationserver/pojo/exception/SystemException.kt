package tech.shali.authorizationserver.pojo.exception

import tech.shali.authorizationserver.pojo.ErrorType
import java.lang.RuntimeException

class SystemException(
    override val message: String,
    val error: ErrorType = ErrorType.ERROR
) : RuntimeException()