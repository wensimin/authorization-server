package tech.shali.authorizationserver.entity.mixin

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.type.WritableTypeId
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import tech.shali.authorizationserver.entity.SysAuth
import tech.shali.authorizationserver.entity.SysUser
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonDeserialize(using = SysUserDeserializer::class)
//@JsonSerialize(using = SysUserSerializer::class)
//@JsonIgnoreProperties(ignoreUnknown = true)
abstract class SysUserMixin {
}


class SysUserDeserializer : StdDeserializer<SysUser>(SysUser::class.java) {

    override fun deserialize(parser: JsonParser, context: DeserializationContext?): SysUser {
        val node: JsonNode = parser.codec.readTree(parser)
        return SysUser(node["username"].textValue(), "").apply {
            id = node["id"].textValue()
            auths = node["auths"].last().map {
                SysAuth.valueOf(it.textValue() ?: it.last().textValue())
            }.toMutableSet()
            createDate = Date(node["createDate"].last().numberValue().toLong())
            updateDate = Date(node["updateDate"].last().numberValue().toLong())
        }
    }

    override fun deserializeWithType(
        p: JsonParser?,
        ctxt: DeserializationContext?,
        typeDeserializer: TypeDeserializer?
    ): Any {
        return super.deserializeWithType(p, ctxt, typeDeserializer)
    }
}

class SysUserSerializer : StdSerializer<SysUser>(SysUser::class.java) {

    override fun serialize(user: SysUser, parser: JsonGenerator, provider: SerializerProvider?) {
        parser.run {
            writeStartObject()
            writeStringField("id", user.id)
            writeStringField("username", user.username)
            writeStringField("auths", user.auths.joinToString(","))
            writeNumberField("createDate", user.createDate.time)
            writeNumberField("updateDate", user.updateDate.time)
            writeEndObject()
        }
    }

    override fun serializeWithType(
        value: SysUser?,
        gen: JsonGenerator?,
        provider: SerializerProvider?,
        typeSer: TypeSerializer
    ) {
        val typeId: WritableTypeId = typeSer.typeId(value, JsonToken.FIELD_NAME)
        typeSer.writeTypePrefix(gen, typeId)
        serialize(value!!, gen!!, provider) // call your customized serialize method

        typeSer.writeTypeSuffix(gen, typeId)
    }

}