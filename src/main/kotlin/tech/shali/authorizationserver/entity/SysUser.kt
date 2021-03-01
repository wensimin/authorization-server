package tech.shali.authorizationserver.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
class SysUser(
    @Column(nullable = false, unique = true)
    private var username: String,
    @Column(nullable = false)
    @JsonIgnore
    private var password: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    val auths: Set<SysAuth> = HashSet(),
) : Data(), UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> {
        return auths.map { auth -> SimpleGrantedAuthority(auth.name) }.toList()
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getPassword() = password

    override fun getUsername() = username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled(): Boolean = true

}