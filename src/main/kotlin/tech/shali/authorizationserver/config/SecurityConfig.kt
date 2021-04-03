package tech.shali.authorizationserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.SecurityFilterChain
import tech.shali.authorizationserver.entity.SysAuth
import tech.shali.authorizationserver.service.SysUserService


@EnableWebSecurity
class SecurityConfig {


    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf().disable()
            .authorizeRequests().apply {
                antMatchers("/login").permitAll()
                antMatchers("/actuator/health").permitAll()
                antMatchers(
                    "/actuator/**", // 端点管理相关
                    "/client/**", // 认证client
                    "/user/auth/**" //用户 auth
                ).hasAuthority(SysAuth.ADMIN.name)
                //register matcher
                mvcMatchers(HttpMethod.POST, "/user/**").hasAuthority(SysAuth.ADMIN.name)
                anyRequest().authenticated()
            }.and().formLogin().apply {
                loginPage("/login")
            }
        return http.build()
    }

    /**
     * 自定义token
     * 把用户的auth添加进去
     */
    @Bean
    fun tokenCustomizer(userService: SysUserService): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context ->
            if (context.tokenType == OAuth2TokenType.ACCESS_TOKEN) {
                val claim: Authentication = context.getPrincipal()
                val user = userService.loadUserByUsername(claim.name)
                context.claims.claim("auth", user.auths)
            }
        }
    }

    // 和temple库冲突 使用\$
    @Bean
    fun providerSettings(@Value("\${system.config.issuer}") url: String): ProviderSettings {
        return ProviderSettings().issuer(url)
    }
}