package com.example.palma.models

//START of DATA-CLASS: Message
data class Message(val userKey: String? = "", val date: String? = "", val time: String? = "", val message: String? = "")
{constructor(): this("", "", "", "")}//END of DATA-CLASS: Message
