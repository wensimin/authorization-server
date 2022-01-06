package tech.shali.authorizationserver.entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import org.hibernate.validator.constraints.Length
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob
import javax.validation.constraints.NotEmpty

/**
 * oauth2 client
 * 目前仅存储必要信息
 */
@Entity
class Oauth2Client(
    @Column(nullable = false, unique = true)
    @get:Length(min = 5, max = 20)
    val clientId: String,
    @Column(nullable = false)
    @get:Length(min = 8, max = 200)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var clientSecret: String,
    /**
     * 当前路径匹配
     */
    @Column(nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    @Lob
    @get:NotEmpty
    val redirectUri: String
) : Data()