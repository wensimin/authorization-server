package tech.shali.authorizationserver.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import tech.shali.authorizationserver.entity.AuthorizationToken
import java.time.Instant

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

    @Modifying
    @Query(
        "delete from AuthorizationToken t where (t.refreshToken IS NOT NULL and t.refreshTokenExpiresAt < :time) " +
                "or (t.refreshToken IS NULL and t.accessToken IS NOT NULL and t.accessTokenExpiresAt < :time) " +
                "or (t.refreshToken IS NULL and t.accessToken IS NULL and t.authorizationCode IS NOT NULL and t.authorizationCodeExpiresAt < :time)"
    )
    fun deleteExpiresToken(time: Instant)
}