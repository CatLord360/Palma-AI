package com.example.palma.models

//START of DATA-CLASS: Contact
data class Contact(val messageKey: String = "", val username: String = "", val mobile: String = "", val email: String = "", val type: String = "")
{constructor() : this("", "", "", "", "")}//END of DATA-CLASS: Contact