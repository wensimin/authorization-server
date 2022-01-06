package tech.shali.authorizationserver.task

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tech.shali.authorizationserver.service.JdbcOAuth2AuthorizationService
import javax.transaction.Transactional

@Component
class TokenTask(private val jdbcOAuth2AuthorizationService: JdbcOAuth2AuthorizationService) {

    /**
     * 当前使用定期清理过期token模式
     * //TODO 框架本身实现
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    fun deleteExpiresToken() {
        jdbcOAuth2AuthorizationService.deleteExpiresToken()
    }
}