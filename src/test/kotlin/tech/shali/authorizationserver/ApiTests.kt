package tech.shali.authorizationserver


import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tech.shali.authorizationserver.entity.Oauth2Client
import tech.shali.authorizationserver.entity.SysAuth
import tech.shali.authorizationserver.entity.SysUser
import tech.shali.authorizationserver.service.Oauth2ClientService
import tech.shali.authorizationserver.service.SysUserService


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ApiTests(
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val oauth2ClientService: Oauth2ClientService,
    @Autowired
    private val sysUserService: SysUserService,
    @Autowired
    private val passwordEncoder: PasswordEncoder

) {
    companion object {
        val client = Oauth2Client("test1", "test", "test://test", true)
//        val user = SysUser("testU", "123456")
        val adminUser = SysUser("admin", "123456", auths = mutableSetOf(SysAuth.ADMIN))
    }

    @Test
    fun noLogin() {
        mockMvc.perform(get("/user"))
            .andExpect(status().isUnauthorized)
            .andExpect(content().string(""))
    }

    @Test
    @Order(1)
    fun createClient() {
        oauth2ClientService.create(client).apply {
            assert(
                clientId == "test1" &&
                        passwordEncoder.matches("test", clientSecret) &&
                        redirectUri == "test://test" &&
                        clientCredentials
            )
        }
        assert(oauth2ClientService.findByClientId("test1")?.clientId == "test1")

    }

    @Test
    @Order(1)
    fun createAdmin() {
        sysUserService.register(adminUser).apply {
            assert(username == "admin")
        }
        assert(sysUserService.loadUserByUsername("admin").username == "admin")

    }


}