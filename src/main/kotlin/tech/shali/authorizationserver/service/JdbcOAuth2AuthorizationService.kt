package tech.shali.authorizationserver.service

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.core.*
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import tech.shali.authorizationserver.dao.AuthorizationDao
import tech.shali.authorizationserver.entity.AuthorizationToken
import java.time.Instant
import java.util.function.Consumer

/**
 * auth token  持久化service
 */
@Service
class JdbcOAuth2AuthorizationService(
    private val authorizationRepository: AuthorizationDao,
    private val registeredClientRepository: RegisteredClientRepository
) : OAuth2AuthorizationService {
    private val objectMapper = ObjectMapper()

    init {
        val classLoader: ClassLoader = JdbcOAuth2AuthorizationService::class.java.classLoader
        val securityModules: MutableList<Module>? = SecurityJackson2Modules.getModules(classLoader)
        objectMapper.registerModule(KotlinModule())
        objectMapper.registerModules(securityModules)
        objectMapper.registerModule(OAuth2AuthorizationServerJackson2Module())
    }

    override fun save(authorization: OAuth2Authorization) {
        this.authorizationRepository.save(toEntity(authorization))
    }

    override fun remove(authorization: OAuth2Authorization) {
        this.authorizationRepository.deleteById(authorization.id)
    }

    override fun findById(id: String): OAuth2Authorization? {
        return this.authorizationRepository.findById(id).map(this::toObject).orElse(null)
    }

    override fun findByToken(token: String, tokenType: OAuth2TokenType?): OAuth2Authorization? {
        return when {
            tokenType == null -> {
                authorizationRepository.findByStateOrAuthorizationCodeOrAccessTokenOrRefreshToken(token)
            }
            OAuth2ParameterNames.STATE == tokenType.value -> {
                authorizationRepository.findByState(token)
            }
            OAuth2ParameterNames.CODE == tokenType.value -> {
                authorizationRepository.findByAuthorizationCode(token)
            }
            OAuth2ParameterNames.ACCESS_TOKEN == tokenType.value -> {
                authorizationRepository.findByAccessToken(token)
            }
            OAuth2ParameterNames.REFRESH_TOKEN == tokenType.value -> {
                authorizationRepository.findByRefreshToken(token)
            }
            else -> null
        }?.let {
            toObject(it)
        }
    }

    private fun toObject(entity: AuthorizationToken): OAuth2Authorization? {
        val registeredClient = registeredClientRepository.findById(entity.registeredClientId)
            ?: throw DataRetrievalFailureException(
                "The RegisteredClient with id '${entity.registeredClientId}' was not found in the RegisteredClientRepository."
            )
        val builder = OAuth2Authorization.withRegisteredClient(registeredClient)
            .id(entity.id)
            .principalName(entity.principalName)
            .authorizationGrantType(resolveAuthorizationGrantType(entity.authorizationGrantType))
            .attributes { attributes: MutableMap<String?, Any?> ->
                attributes.putAll(
                    parseMap(entity.attributes)
                )
            }
        if (entity.state != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.state)
        }
        if (entity.authorizationCode != null) {
            val authorizationCode = OAuth2AuthorizationCode(
                entity.authorizationCode,
                entity.authorizationCodeIssuedAt,
                entity.authorizationCodeExpiresAt
            )
            builder.token(authorizationCode,
                { metadata: MutableMap<String?, Any?> ->
                    metadata.putAll(
                        parseMap(entity.authorizationCodeMetadata)
                    )
                })
        }
        if (entity.accessToken != null) {
            val accessToken = OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                entity.accessToken,
                entity.accessTokenIssuedAt,
                entity.accessTokenExpiresAt
            )
            builder.token(accessToken,
                { metadata: MutableMap<String?, Any?> ->
                    metadata.putAll(
                        parseMap(entity.accessTokenMetadata)
                    )
                })
        }
        if (entity.refreshToken != null) {
            val refreshToken = OAuth2RefreshToken(
                entity.refreshToken,
                entity.refreshTokenIssuedAt,
                entity.refreshTokenExpiresAt
            )
            builder.token(refreshToken,
                { metadata: MutableMap<String?, Any?> ->
                    metadata.putAll(
                        parseMap(entity.refreshTokenMetadata)
                    )
                })
        }
        if (entity.idToken != null) {
            val idToken = OidcIdToken(
                entity.idToken,
                entity.idTokenIssuedAt,
                entity.idTokenExpiresAt,
                parseMap(entity.idTokenClaims)
            )
            builder.token(idToken,
                { metadata: MutableMap<String?, Any?> ->
                    metadata.putAll(
                        parseMap(entity.idTokenMetadata)
                    )
                })
        }
        return builder.build()
    }

    private fun toEntity(authorization: OAuth2Authorization): AuthorizationToken {
        val entity = AuthorizationToken(
            authorization.id,
            authorization.registeredClientId,
            authorization.principalName,
            authorization.authorizationGrantType.value,
            writeMap(authorization.attributes),
            authorization.getAttribute(OAuth2ParameterNames.STATE)
        )
        val authorizationCode = authorization.getToken(
            OAuth2AuthorizationCode::class.java
        )
        setTokenValues(
            authorizationCode,
            entity::authorizationCode::set,
            entity::authorizationCodeIssuedAt::set,
            entity::authorizationCodeExpiresAt::set,
            entity::authorizationCodeMetadata::set
        )
        val accessToken = authorization.getToken(
            OAuth2AccessToken::class.java
        )
        setTokenValues(
            accessToken,
            entity::accessToken::set,
            entity::accessTokenIssuedAt::set,
            entity::accessTokenExpiresAt::set,
            entity::accessTokenMetadata::set
        )
        if (accessToken != null && accessToken.token.scopes != null) {
            entity.accessTokenScopes = StringUtils.collectionToDelimitedString(accessToken.token.scopes, ",")
        }
        val refreshToken = authorization.getToken(
            OAuth2RefreshToken::class.java
        )
        setTokenValues(
            refreshToken,
            entity::refreshToken::set,
            entity::refreshTokenIssuedAt::set,
            entity::refreshTokenExpiresAt::set,
            entity::refreshTokenMetadata::set
        )
        val oidcIdToken = authorization.getToken(OidcIdToken::class.java)
        setTokenValues(
            oidcIdToken,
            entity::idToken::set,
            entity::idTokenIssuedAt::set,
            entity::idTokenExpiresAt::set,
            entity::idTokenMetadata::set
        )
        if (oidcIdToken != null) {
            entity.idTokenClaims = writeMap(oidcIdToken.claims!!)
        }
        return entity
    }


    private fun setTokenValues(
        token: OAuth2Authorization.Token<*>?,
        tokenValueConsumer: Consumer<String>,
        issuedAtConsumer: Consumer<Instant?>,
        expiresAtConsumer: Consumer<Instant?>,
        metadataConsumer: Consumer<String>
    ) {
        if (token != null) {
            val oauth2Token: OAuth2Token = token.token
            tokenValueConsumer.accept(oauth2Token.tokenValue)
            issuedAtConsumer.accept(oauth2Token.issuedAt)
            expiresAtConsumer.accept(oauth2Token.expiresAt)
            metadataConsumer.accept(writeMap(token.metadata))
        }
    }

    private fun parseMap(data: String?): Map<String, Any> {
        return try {
            if (data == null) emptyMap() else objectMapper.readValue(data)
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex.message, ex)
        }
    }

    private fun writeMap(metadata: Map<String, Any>): String {
        return try {
            objectMapper.writeValueAsString(metadata)
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex.message, ex)
        }
    }

    private fun resolveAuthorizationGrantType(authorizationGrantType: String): AuthorizationGrantType {
        return when {
            AuthorizationGrantType.AUTHORIZATION_CODE.value == authorizationGrantType -> {
                AuthorizationGrantType.AUTHORIZATION_CODE
            }
            AuthorizationGrantType.CLIENT_CREDENTIALS.value == authorizationGrantType -> {
                AuthorizationGrantType.CLIENT_CREDENTIALS
            }
            AuthorizationGrantType.REFRESH_TOKEN.value == authorizationGrantType -> {
                AuthorizationGrantType.REFRESH_TOKEN
            }
            // Custom authorization grant type
            else -> AuthorizationGrantType(authorizationGrantType)
        }
    }
}