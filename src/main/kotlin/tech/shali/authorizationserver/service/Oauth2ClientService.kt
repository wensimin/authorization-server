package tech.shali.authorizationserver.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.stereotype.Service
import tech.shali.authorizationserver.dao.Oauth2ClientDao
import tech.shali.authorizationserver.entity.Oauth2Client
import java.time.Duration

@Service
class Oauth2ClientService(
    private val oauth2ClientDao: Oauth2ClientDao,
    private val passwordEncoder: PasswordEncoder
) :
    RegisteredClientRepository {

    override fun save(registeredClient: RegisteredClient) {
        save(
            Oauth2Client(
                registeredClient.clientId,
                registeredClient.clientSecret!!,
                // 目前使用单一string存储redirect url 并且有超长问题
                registeredClient.redirectUris.first()
            )
        )
    }

    override fun findById(id: String): RegisteredClient? {
        return oauth2ClientDao.findById(id).orElse(null)?.let { e -> getClient(e) }
    }

    override fun findByClientId(clientId: String): RegisteredClient? {
        return oauth2ClientDao.findByClientId(clientId)?.let { e -> getClient(e) }
    }

    fun save(oauth2Client: Oauth2Client): Oauth2Client {
        return oauth2ClientDao.save(oauth2Client.apply {
            this.clientSecret = passwordEncoder.encode(clientSecret)
        })
    }

    /**
     * 使用默认配置构建client
     * 配置目前hard code
     */
    private fun getClient(oauth2Client: Oauth2Client): RegisteredClient? {
        return RegisteredClient.withId(oauth2Client.id)
            .clientId(oauth2Client.clientId)
            .clientSecret(oauth2Client.clientSecret)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri(oauth2Client.redirectUri)
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .tokenSettings(
                TokenSettings.builder().apply {
                    accessTokenTimeToLive(Duration.ofHours(1))
                    refreshTokenTimeToLive(Duration.ofDays(30))
                }.build()
            )
            // 需要用户允许 请求scope仅openid时不会触发
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build()
    }

}