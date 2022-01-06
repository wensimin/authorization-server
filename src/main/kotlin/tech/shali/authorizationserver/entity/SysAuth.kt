package tech.shali.authorizationserver.entity

enum class SysAuth {
    ADMIN, R18,

    /**
     *带有此auth的user为client
     */
    CLIENT
}
