package tech.shali.authorizationserver.config

import org.springframework.context.annotation.Bean
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
            .authorizeRequests { authorizeRequests ->
                authorizeRequests.antMatchers("/login").permitAll()
                authorizeRequests.antMatchers("/actuator/health").permitAll()
                authorizeRequests.antMatchers("/actuator/**").hasAuthority(SysAuth.ADMIN.name)
                //register matcher
                authorizeRequests.mvcMatchers(HttpMethod.POST, "/user/**").hasAuthority(SysAuth.ADMIN.name)
                authorizeRequests.anyRequest().authenticated()
            }.formLogin { config ->
                config.loginPage("/login")
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

    @Bean
    fun providerSettings(): ProviderSettings? {
        return ProviderSettings().issuer("http://127.0.0.1:81/authorization")
    }
}