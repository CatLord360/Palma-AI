package com.example.palma.models

//START of DATA-CLASS: Greeting
data class Greeting(val userKey: String? = "", val date: String? = "", val time: String? = "", val message: String? = "", val greeting: String? = "")
{constructor(): this("", "", "", "", "")}//END of DATA-CLASS: Greeting
