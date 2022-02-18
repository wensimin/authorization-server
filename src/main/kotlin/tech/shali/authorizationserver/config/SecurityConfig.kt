package tech.shali.authorizationserver.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2TokenType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer
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

            } //OAUTH2 server配置,似乎只对userinfo端口起效
//            .and().oauth2ResourceServer().jwt().and()
            .and().formLogin().apply {
                loginPage("/login")
            }
        return http.build()
    }

    // jwt decoder 配置oauth2 rs必须
//    @Bean
//    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder? {
//        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
//    }


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
                //使用toSet 会在size=0的时候生成emptySet导致不被jackson识别报错,改用toHashSet
                context.claims.claim("auth", user.authorities.map { it.authority }.toHashSet())
            }
        }
    }


}