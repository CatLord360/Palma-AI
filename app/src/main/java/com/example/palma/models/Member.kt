package com.example.palma.models

//START of DATA-CLASS: Member
data class Member(val username: String = "", val mobile: String = "", val email: String = "", val type: String = "")
{constructor() : this("", "", "", "")}//END of DATA-CLASS: Member
