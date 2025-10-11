package com.example.palma.models

//START of DATA-CLASS: Daily
data class Daily(val userKey: String? = "", val type: String? = "", val time: String? = "", val reminder: String? = "")
{constructor(): this("", "", "", "")}//END of DATA-CLASS: Daily
