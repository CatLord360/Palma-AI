package com.example.palma.models

//START of DATA-CLASS: User
data class User(val username: String? = "", val gender: String?, val birthdate: String?, val mobile: String? = "", val email: String? = "")
{constructor(): this("", "", "", "", "")}//END of DATA-CLASS: User
//END of DATA-CLASS: User
