package tech.shali.authorizationserver.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.stereotype.Service
import tech.shali.authorizationserver.dao.Oauth2ClientDao
import tech.shali.authorizationserver.entity.Oauth2Client
import tech.shali.authorizationserver.entity.SysAuth
import tech.shali.authorizationserver.entity.SysUser
import java.time.Duration

@Service
class Oauth2ClientService(
    private val oauth2ClientDao: Oauth2ClientDao,
    private val passwordEncoder: PasswordEncoder,
    private val sysUserService: SysUserService
) :
    RegisteredClientRepository {

    override fun save(registeredClient: RegisteredClient) {
        throw TODO("未实现new client")
    }

    override fun findById(id: String): RegisteredClient? {
        return oauth2ClientDao.findById(id).orElse(null)?.let { e -> getClient(e) }
    }

    override fun findByClientId(clientId: String): RegisteredClient? {
        return oauth2ClientDao.findByClientId(clientId)?.let { e -> getClient(e) }
    }

    /**
     * new client
     */
    fun create(oauth2Client: Oauth2Client): Oauth2Client {
        // 如果允许client模式登录则需要同时建立user
        if (oauth2Client.clientCredentials) {
            val clientUser =
                SysUser(oauth2Client.clientId, oauth2Client.clientSecret).apply { auths.add(SysAuth.CLIENT) }
            sysUserService.register(clientUser)
        }
        return oauth2ClientDao.save(oauth2Client.apply {
            this.clientSecret = passwordEncoder.encode(clientSecret)
        })
    }

    /**
     * 使用默认配置构建client
     * 配置目前hard code
     *
     */
    private fun getClient(oauth2Client: Oauth2Client): RegisteredClient? {
        return RegisteredClient.withId(oauth2Client.id).apply {
            clientId(oauth2Client.clientId)
            clientSecret(oauth2Client.clientSecret)
            authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            if (oauth2Client.clientCredentials) authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            redirectUris {
                it.addAll(oauth2Client.redirectUri.split(","))
            }
            scope(OidcScopes.OPENID)
            tokenSettings(
                TokenSettings.builder().apply {
                    accessTokenTimeToLive(Duration.ofHours(1))
                    refreshTokenTimeToLive(Duration.ofDays(30))
                }.build()
            )
            // 需要用户允许 请求scope仅openid时不会触发
            clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
        }.build()
    }

}