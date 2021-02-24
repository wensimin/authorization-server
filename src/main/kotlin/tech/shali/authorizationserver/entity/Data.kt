package tech.shali.authorizationserver.entity

import tech.shali.authorizationserver.entity.listener.DataEntityListener
import java.util.*
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(DataEntityListener::class)
open class Data(
    @Column(nullable = false) var createDate: Date = Date(),
    @Column(nullable = false) var updateDate: Date = Date(),
    @Id @Column(nullable = false) var id: String = UUID.randomUUID().toString()
) {

    fun beforeUpdate() {
        updateDate = Date()
    }
}