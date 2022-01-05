package tech.shali.authorizationserver.entity.mixin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class HashSetMixin @JsonCreator constructor(set: Set<*>?)