
package com.aquila.pocxpertalerts.data.model

import com.google.gson.annotations.SerializedName

data class SubscriptionDto(

    @SerializedName("userSubId")
    val userSubId: Long = 0L,

    @SerializedName("applicationId")
    val applicationId: Long = 0L,

    @SerializedName("applicationName")
    val applicationName: String? = null,

    @SerializedName("alertDefName")
    val alertDefName: String? = null,

    // API uses "alerName", not "alertName".
    @SerializedName("alerName")
    val alertName: String? = null,

    @SerializedName("alertFlag")
    val alertFlag: Int = 1,

    @SerializedName("device")
    val device: String? = null,

    // "email" represents the Mail notification flag: Yes/No.
    @SerializedName("email")
    val emailEnabled: String? = null,

    // Actual email address, if returned by the details API.
    @SerializedName("emailId")
    val emailId: String? = null,

    @SerializedName("active")
    val active: Int = 1,

    @SerializedName("alertDefId")
    val alertDefId: Long = 0L,

    @SerializedName("aggregateJobsId")
    val aggregateJobsId: Long = 0L,

    @SerializedName("alertTypeId")
    val alertTypeId: String? = null,

    @SerializedName("body")
    val body: String? = null,

    @SerializedName("subject")
    val subject: String? = null,

    @SerializedName("threshold")
    val threshold: Int = 0,

    @SerializedName("sortOrder")
    val sortOrder: Int = 0,

    @SerializedName("errorCode")
    val errorCode: Int = 0,

    @SerializedName("errorMessage")
    val errorMessage: String? = null,

    @SerializedName("message")
    val message: String? = null
)
