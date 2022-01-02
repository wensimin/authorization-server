package tech.shali.authorizationserver.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.stereotype.Service
import tech.shali.authorizationserver.dao.SysUserDao
import tech.shali.authorizationserver.entity.SysUser
import tech.shali.authorizationserver.pojo.AuthVo
import tech.shali.authorizationserver.pojo.RegisterVo
import tech.shali.authorizationserver.pojo.exception.SystemException
import java.security.Principal

@Service
class SysUserService(
    private val userDao: SysUserDao,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): SysUser {
        return userDao.findByUsername(username) ?: throw UsernameNotFoundException("未找到用户")
    }

    fun register(vo: RegisterVo): SysUser {
        val username = vo.username!!
        userDao.findByUsername(username)?.let { throw SystemException("用户已经存在") }
        val user = SysUser(username, vo.password!!)
        user.password = passwordEncoder.encode(user.password)
        return userDao.save(user)
    }

    fun info(principal: Principal): SysUser {
        return loadUserByUsername(principal.name)
    }

    fun addAuth(auth: AuthVo): SysUser {
        val user = loadUserByUsername(auth.username!!)
        user.auths.add(auth.auth!!)
        return this.userDao.save(user)
    }

}
