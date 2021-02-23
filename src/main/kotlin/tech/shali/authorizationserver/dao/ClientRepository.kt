package tech.shali.authorizationserver.dao

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Service

@Service
class ClientRepository : RegisteredClientRepository {
    override fun findById(id: String?): RegisteredClient? {
        TODO("Not yet implemented")
    }

    override fun findByClientId(clientId: String?): RegisteredClient? {
        TODO("Not yet implemented")
    }
}