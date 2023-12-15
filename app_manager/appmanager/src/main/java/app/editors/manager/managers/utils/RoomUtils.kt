package app.editors.manager.managers.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.editors.manager.R


data class RoomInfo(@DrawableRes val icon: Int, @StringRes val title: Int, @StringRes val description: Int)

object RoomUtils {

    fun getRoomInfo(type: Int): RoomInfo {
        val icon = when (type) {
            2 -> R.drawable.ic_collaboration_room
            6 -> R.drawable.ic_public_room
            5 -> R.drawable.ic_custom_room
            else -> {
                R.drawable.ic_collaboration_room
            }
        }
        val title = when (type) {
            2 -> R.string.rooms_add_collaboration
            6 -> R.string.rooms_add_public_room
            5 -> R.string.rooms_add_custom
            else -> {
                R.string.rooms_add_collaboration
            }
        }
        val des = when (type) {
            2 -> R.string.rooms_add_collaboration_des
            6 -> R.string.rooms_add_public_room_des
            5 -> R.string.rooms_add_custom_des
            else -> {
                R.string.rooms_add_collaboration_des
            }
        }
        return RoomInfo(icon, title, des)
    }

    fun getRoomInitials(title: String): String? {
        return try {
            when (title.length) {
                1 -> title[0].toString()
                2 -> title.split("").let { "${it[0][0]}${it[1][0]}" }
                else -> title.split("").let { "${it[0][0]}${it[it.lastIndex][0]}" }
            }
        } catch (_: IndexOutOfBoundsException) {
            null
        }
    }

}