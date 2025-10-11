package com.example.palma.models

//START of DATA-CLASS: Monthly
data class Monthly(val userKey: String? = "", val type: String? = "", val date: String? = "", val time: String? = "", val reminder: String? = "")
{constructor(): this("", "", "", "", "")}//END of DATA-CLASS: Monthly
