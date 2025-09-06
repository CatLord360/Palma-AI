package com.example.palma.models

//START of DATA-CLASS:
data class Reminder(val userKey: String? = "", val type: String? = "", val date: String? = "", val time: String? = "", val reminder: String? = "")
{constructor(): this("", "", "", "", "")}//END of DATA-CLASS: Reminder
