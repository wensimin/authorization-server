package tech.shali.authorizationserver.entity

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
enum class SysAuth {
    ADMIN, R18
}
