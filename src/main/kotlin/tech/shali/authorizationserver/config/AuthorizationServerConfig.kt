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
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
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
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
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


    // 和temple库冲突 使用\$
    @Bean
    fun providerSettings(@Value("\${spring.security.oauth2.resource-server.jwt.issuer-uri}") url: String): ProviderSettings =
        ProviderSettings.builder().issuer(url).build()
}