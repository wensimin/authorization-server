package tech.shali.authorizationserver.pojo

class ErrorResponse(val type: ErrorType, val message: String)
enum class ErrorType {
    ERROR,PARAM
}
