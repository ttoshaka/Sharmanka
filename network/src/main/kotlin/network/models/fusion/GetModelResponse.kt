package network.models.fusion

import com.google.gson.annotations.SerializedName

data class GetModelResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("version")
    val version: Double,
    @SerializedName("type")
    val type: String
)