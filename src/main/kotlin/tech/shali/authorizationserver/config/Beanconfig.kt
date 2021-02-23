package tech.shali.authorizationserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager


@Configuration
class BeanConfig {
    @Bean
    fun users(): UserDetailsService {
        val user = User.withDefaultPasswordEncoder()
            .username("user1")
            .password("password")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }


}