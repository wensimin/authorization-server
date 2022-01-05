package tech.shali.authorizationserver.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.authority.SimpleGrantedAuthority
import tech.shali.authorizationserver.pojo.User
import javax.persistence.*


@Entity
class SysUser(
    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = false)
    @JsonIgnore
    var password: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    var auths: MutableSet<SysAuth> = HashSet(),
) : Data() {


    /**
     * 转换为principal类
     */
    fun toUser(): User {
        return User(username, password, auths.map { auth -> SimpleGrantedAuthority(auth.name) }.toMutableList())
    }
}
