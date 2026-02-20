package alexey.odintsov.dltstudio.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PluginStateEntity(
    @PrimaryKey
    val pluginClass: String,
    val enabled: Boolean = true,
)