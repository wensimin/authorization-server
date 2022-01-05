package tech.shali.authorizationserver.entity

import org.hibernate.annotations.Type
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob

/**
 * 持久化token实体
 */
@Entity
class AuthorizationToken(
    override var id: String,
    @Column(nullable = false)
    var registeredClientId: String,

    @Column(nullable = false)
    var principalName: String,

    @Column(nullable = false)
    var authorizationGrantType: String,
    @Column(nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    @Lob
    var attributes: String,
    @Column(length = 500)
    var state: String?,

    @Type(type = "org.hibernate.type.TextType")
    @Lob
    var authorizationCode: String? = null,
    var authorizationCodeIssuedAt: Instant? = null,
    var authorizationCodeExpiresAt: Instant? = null,
    var authorizationCodeMetadata: String? = null,

    @Type(type = "org.hibernate.type.TextType")
    @Lob
    var accessToken: String? = null,
    var accessTokenIssuedAt: Instant? = null,
    var accessTokenExpiresAt: Instant? = null,

    @Column(length = 2000)
    var accessTokenMetadata: String? = null,

    @Column(length = 1000)
    var accessTokenScopes: String? = null,

    @Type(type = "org.hibernate.type.TextType")
    @Lob
    var refreshToken: String? = null,
    var refreshTokenIssuedAt: Instant? = null,
    var refreshTokenExpiresAt: Instant? = null,

    @Column(length = 2000)
    var refreshTokenMetadata: String? = null,

    @Type(type = "org.hibernate.type.TextType")
    @Lob
    var idToken: String? = null,
    var idTokenIssuedAt: Instant? = null,
    var idTokenExpiresAt: Instant? = null,

    @Column(length = 2000)
    var idTokenMetadata: String? = null,

    @Column(length = 2000)
    var idTokenClaims: String? = null
) : Data()