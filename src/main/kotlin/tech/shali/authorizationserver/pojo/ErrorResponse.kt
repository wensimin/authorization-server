package tech.shali.authorizationserver.pojo

class ErrorResponse(val error: ErrorType, val message: String)
enum class ErrorType {
    ERROR,PARAM
}
