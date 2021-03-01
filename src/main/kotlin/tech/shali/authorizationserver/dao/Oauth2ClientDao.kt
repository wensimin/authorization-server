package tech.shali.authorizationserver.dao

import org.springframework.data.jpa.repository.JpaRepository
import tech.shali.authorizationserver.entity.Oauth2Client
import tech.shali.authorizationserver.entity.SysUser

interface Oauth2ClientDao : JpaRepository<Oauth2Client, String> {
    fun findByClientId(clientId: String): Oauth2Client?
}