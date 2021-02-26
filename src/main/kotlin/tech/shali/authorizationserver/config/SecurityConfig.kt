package tech.shali.authorizationserver.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import tech.shali.authorizationserver.entity.SysAuth

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
}