package tech.shali.authorizationserver.pojo

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * principal类
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
class User(
    private var username: String,
    private var password: String,
    private var authorities: MutableCollection<out GrantedAuthority>,
    private var accountNonExpired: Boolean = true,
    private var accountNonLocked: Boolean = true,
    private var credentialsNonExpired: Boolean = true,
    private var enabled: Boolean = true

) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = accountNonExpired

    override fun isAccountNonLocked(): Boolean = accountNonLocked

    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isEnabled(): Boolean = enabled
}