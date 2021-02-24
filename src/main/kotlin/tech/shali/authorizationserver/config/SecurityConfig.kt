package tech.shali.authorizationserver.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import tech.shali.authorizationserver.entity.SysAuth

@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .authorizeRequests { authorizeRequests ->
                authorizeRequests.mvcMatchers(HttpMethod.POST, "/user/**").hasAuthority(SysAuth.ADMIN.name)
                authorizeRequests.mvcMatchers("/user/**").authenticated().and().csrf().disable()
                authorizeRequests.anyRequest().authenticated().and().formLogin(Customizer.withDefaults())
            }

        return http.build()
    }
}