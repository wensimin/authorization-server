package tech.shali.authorizationserver.handler

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import tech.shali.authorizationserver.pojo.ErrorResponse
import tech.shali.authorizationserver.pojo.ErrorType
import tech.shali.authorizationserver.pojo.exception.SystemException

@ControllerAdvice
@ResponseBody
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [Exception::class])
    fun exception(e: java.lang.Exception): ErrorResponse {
        e.printStackTrace()
        return ErrorResponse(ErrorType.ERROR, e.localizedMessage)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun exception(e: MethodArgumentNotValidException): ErrorResponse {
        val fieldError = e.bindingResult.fieldError
        val message = fieldError!!.field + ":" + fieldError.defaultMessage
        return ErrorResponse(ErrorType.PARAM, message)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [SystemException::class])
    fun exception(e: SystemException): ErrorResponse {
        return ErrorResponse(e.type, e.message)
    }

}