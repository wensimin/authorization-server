package tech.shali.authorizationserver.entity.listener

import tech.shali.authorizationserver.entity.Data
import javax.persistence.PreUpdate

class DataEntityListener {
    @PreUpdate
    fun methodExecuteBeforeUpdate(reference: Data) {
        reference.beforeUpdate()
    }
}
