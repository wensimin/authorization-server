package tech.shali.authorizationserver.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import tech.shali.authorizationserver.entity.AuthorizationToken

interface AuthorizationDao : JpaRepository<AuthorizationToken, String> {
    fun findByState(state: String): AuthorizationToken?
    fun findByAuthorizationCode(authorizationCode: String): AuthorizationToken?
    fun findByAccessToken(accessToken: String): AuthorizationToken?
    fun findByRefreshToken(refreshToken: String): AuthorizationToken?

    @Query(
        "select a from AuthorizationToken a where a.state = :token" +
                " or a.authorizationCode = :token" +
                " or a.accessToken = :token" +
                " or a.refreshToken = :token"
    )
    fun findByStateOrAuthorizationCodeOrAccessTokenOrRefreshToken(token: String): AuthorizationToken?

}