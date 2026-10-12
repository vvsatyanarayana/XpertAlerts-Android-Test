
package com.aquila.pocxpertalerts.data.model

import com.google.gson.annotations.SerializedName

data class AlertDto(
    @SerializedName("id")
    val id: Long = 0L,

    @SerializedName("alertId")
    val alertId: Int = 0,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("subject")
    val subject: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("alertSendDate")
    val alertSendDate: String? = null,

    @SerializedName("groupBy")
    val groupBy: String? = null,

    @SerializedName("errorCode")
    val errorCode: Int = 0,

    @SerializedName("archiveId")
    val archiveId: Long = 0L,

    @SerializedName("errorMessage")
    val errorMessage: String? = null,

    @SerializedName("aggregateAlertId")
    val aggregateAlertId: String? = null,

    @SerializedName("alertDefId")
    val alertDefId: Int = 0,

    @SerializedName("applicationId")
    val applicationId: Long = 0L,

    @SerializedName("buttonDetails")
    val buttonDetails: String? = null
)
