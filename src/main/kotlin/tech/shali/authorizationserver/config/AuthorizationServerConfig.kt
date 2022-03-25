package tech.shali.authorizationserver.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*


@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfigurer<HttpSecurity>().run {
            authorizationEndpoint {
                it.consentPage("/oauth2/consent")
            }
            http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests { it.anyRequest().authenticated() }
                .csrf { it.ignoringRequestMatchers(endpointsMatcher) }
                .apply(this)
        }
        return http.formLogin().apply {
            loginPage("/login")
        }.and().build()
    }

    /**
     * jwk 存在配置文件时使用配置文件，不存在时使用临时生成
     */
    @Bean
    fun jwkSource(): JWKSource<SecurityContext?> {
        val jwkSet = JWKSet(getRsaKey())
        return JWKSource { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    private fun getRsaKey(): RSAKey {
        return ClassPathResource("jwk.json").let { it ->
            if (!it.exists()) {
                createRSAKey()
            } else {
                JWK.parse(it.file.readText()).toRSAKey()
            }
        }
    }

    private fun createRSAKey(): RSAKey {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        return RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }

    /**
     * 记住用户已授权的scope service,这里设置为不进行记住
     */
    @Bean
    fun authorizationConsentService(): OAuth2AuthorizationConsentService {
        return object : OAuth2AuthorizationConsentService {
            override fun save(authorizationConsent: OAuth2AuthorizationConsent?) {}
            override fun remove(authorizationConsent: OAuth2AuthorizationConsent?) {}
            override fun findById(registeredClientId: String?, principalName: String?): OAuth2AuthorizationConsent? =
                null
        }
    }

    // 和temple库冲突 使用\$
    @Bean
    fun providerSettings(@Value("\${spring.security.oauth2.resource-server.jwt.issuer-uri}") url: String): ProviderSettings =
        ProviderSettings.builder().issuer(url).build()
}