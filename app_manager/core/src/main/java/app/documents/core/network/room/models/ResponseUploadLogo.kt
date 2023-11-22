package app.documents.core.network.room.models

import app.documents.core.network.common.models.BaseResponse
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUploadLogo(val data: String = ""): BaseResponse()