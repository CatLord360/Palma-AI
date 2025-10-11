package com.example.palma.models

//START of DATA-CLASS: Weekly
data class Weekly(val userKey: String? = "", val type: String? = "", val day: String? = "", val time: String? = "", val reminder: String? = "")
{constructor(): this("", "", "", "", "")}//END of DATA-CLASS: Weekly
