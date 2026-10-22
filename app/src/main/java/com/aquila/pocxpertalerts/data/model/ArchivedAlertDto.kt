package com.aquila.pocxpertalerts.data.model

import com.google.gson.JsonObject

data class ArchivedAlertDto(
    val archiveId: Long = 0L,
    val alertId: Int = 0,
    val aggregateAlertId: String = "",
    val alertDefId: Int = 0,
    val applicationId: Long = 0L,
    val message: String = "",
    val subject: String = "",
    val alertSendDate: String = "",
    val groupBy: String = "",
    val errorCode: Int = 0,
    val errorMessage: String = ""
) {
    companion object {
        fun fromJson(json: JsonObject): ArchivedAlertDto {
            fun string(vararg keys: String): String {
                for (key in keys) {
                    val value = json.get(key)
                    if (value != null && !value.isJsonNull) {
                        return runCatching { value.asString }.getOrDefault(value.toString().trim('"'))
                    }
                }
                return ""
            }
            fun long(vararg keys: String): Long = string(*keys).toLongOrNull() ?: 0L
            fun int(vararg keys: String): Int = string(*keys).toIntOrNull() ?: 0

            return ArchivedAlertDto(
                archiveId = long("archiveId", "archiveID", "id", "amsId"),
                alertId = int("alertId", "alertID"),
                aggregateAlertId = string("aggregateAlertId", "aggregateAlertID"),
                alertDefId = int("alertDefId", "alertDefinitionId", "alertDefID"),
                applicationId = long("applicationId", "applicationID"),
                message = string("message", "description", "alertMessage"),
                subject = string("subject", "alertName", "title"),
                alertSendDate = string("alertSendDate", "sendDate", "date", "createdDate"),
                groupBy = string("groupBy", "applicationName", "alertDefinitionName"),
                errorCode = int("errorCode"),
                errorMessage = string("errorMessage", "errorMsg", "message")
            )
        }
    }
}
