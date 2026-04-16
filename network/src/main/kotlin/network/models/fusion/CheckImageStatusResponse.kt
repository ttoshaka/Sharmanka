package network.models.fusion

import com.google.gson.annotations.SerializedName

data class CheckImageStatusResponse(
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("errorDescription")
    val errorDescription: String,
    @SerializedName("censored")
    val censored: Boolean
)
