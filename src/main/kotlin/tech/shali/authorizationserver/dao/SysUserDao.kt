package tech.shali.authorizationserver.dao

import org.springframework.data.jpa.repository.JpaRepository
import tech.shali.authorizationserver.entity.SysUser

interface SysUserDao : JpaRepository<SysUser, String> {
    fun findByUsername(username: String): SysUser?
}