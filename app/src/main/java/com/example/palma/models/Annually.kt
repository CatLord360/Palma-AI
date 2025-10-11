package com.example.palma.models

//START of DATA-CLASS: Annually
data class Annually(val userKey: String? = "", val type: String? = "", val date: String? = "", val time: String? = "", val reminder: String? = "")
{constructor(): this("", "", "", "", "")}//END of DATA-CLASS: Annually
