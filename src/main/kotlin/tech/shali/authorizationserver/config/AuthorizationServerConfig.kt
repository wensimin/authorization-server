package tech.shali.authorizationserver.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.*


@Configuration
@Import(OAuth2AuthorizationServerConfiguration::class)
class AuthorizationServerConfig {

    @Bean
    fun jwkSource(): JWKSource<SecurityContext?> {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey: RSAKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository? {
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("messaging-client")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            //允许回调的 urlList ? 全路径匹配? FIXME 待研究
            .redirectUri("http://localhost:8080/login/oauth2/code/messaging-client-oidc")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .tokenSettings { setting ->
                setting.accessTokenTimeToLive(Duration.ofHours(1))
                setting.refreshTokenTimeToLive(Duration.ofDays(30))
            }
            // 需要用户允许 仅请求scope openid时不会触发
            .clientSettings { clientSettings: ClientSettings -> clientSettings.requireUserConsent(true) }
            .build()
        return InMemoryRegisteredClientRepository(registeredClient)
    }


}