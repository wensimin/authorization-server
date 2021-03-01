package tech.shali.authorizationserver.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.shali.authorizationserver.entity.Oauth2Client
import tech.shali.authorizationserver.service.Oauth2ClientService
import javax.validation.Valid

@RestController
@RequestMapping("client")
class Oauth2ClientController(private val oauth2ClientService: Oauth2ClientService) {

    @PostMapping
    fun create(@RequestBody @Valid oauth2Client: Oauth2Client): Oauth2Client {
        return oauth2ClientService.create(oauth2Client)
    }
}