
package com.aquila.pocxpertalerts.data.repository

import android.content.Context

class ArchivedSearchPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "archived_search_preferences",
        Context.MODE_PRIVATE
    )

    fun load(): ArchivedSearchCriteria {
        return ArchivedSearchCriteria(
            keyword = preferences.getString("keyword", "").orEmpty(),
            startDate = preferences.getString("startDate", "").orEmpty(),
            endDate = preferences.getString("endDate", "").orEmpty(),
            viewByFlag = preferences.getInt("viewByFlag", 0),
            deviceStatusSelection =
                preferences.getInt("deviceStatusSelection", 0),
            emailStatusSelection =
                preferences.getInt("emailStatusSelection", 0)
        )
    }

    fun save(criteria: ArchivedSearchCriteria) {
        preferences.edit()
            .putString("keyword", criteria.keyword)
            .putString("startDate", criteria.startDate)
            .putString("endDate", criteria.endDate)
            .putInt("viewByFlag", criteria.viewByFlag)
            .putInt(
                "deviceStatusSelection",
                criteria.deviceStatusSelection
            )
            .putInt(
                "emailStatusSelection",
                criteria.emailStatusSelection
            )
            .apply()
    }
}
