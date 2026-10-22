package com.aquila.pocxpertalerts.data.repository

data class ArchivedSearchCriteria(
    val keyword: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val viewByFlag: Int = 0,
    val deviceStatusSelection: Int = 0,
    val emailStatusSelection: Int = 0
)