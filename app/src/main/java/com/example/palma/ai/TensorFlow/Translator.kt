package com.example.palma.ai.TensorFlow

//START of CLASS: Translator
class Translator{
    //START of FUNCTION: command
    fun command(prompt: String): String{
        val text = prompt.lowercase().trim()

        //START of IF-STATEMENT:
        if(text.contains("list")){

            //START of IF-STATEMENT:
            if (text.contains("show") || text.contains("display")){
                return "#list command"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("create") || text.contains("make")){
                val name = Regex("named (\\w+)").find(text)?.groupValues?.get(1) ?: "default"
                val type = if (text.contains("private")) "private" else "public"
                return "#list new $name $type"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("load") || text.contains("open")){
                val name = Regex("(list )?(\\w+)").find(text)?.groupValues?.get(2) ?: "default"
                return "#list load $name"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if (text.contains("delete") || text.contains("remove")){
                val name = Regex("(list )?(\\w+)").find(text)?.groupValues?.get(2) ?: "default"
                return "#list delete $name"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if (text.contains("add")){
                val item = Regex("add (.+?) to").find(text)?.groupValues?.get(1) ?: "item"
                val name = Regex("to (.+?) list").find(text)?.groupValues?.get(1) ?: "default"
                return "#list add $name $item"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT
            if(text.contains("remove")){
                val item = Regex("remove (.+?) from").find(text)?.groupValues?.get(1) ?: "item"
                val name = Regex("from (.+?) list").find(text)?.groupValues?.get(1) ?: "default"
                return "#list remove $name $item"
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(text.contains("remind") || text.contains("reminder")){
            val time = Regex("(\\d{1,2}):(\\d{2})").find(text)?.value ?: "00:00"

            //START of IF-STATEMENT:
            if(text.contains("daily") || text.contains("every day")){
                val msg = text.substringAfter(time, "reminder")
                return "#reminder set daily $time $msg"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("monday") || text.contains("tuesday") || text.contains("wednesday") || text.contains("thursday") || text.contains("friday") || text.contains("saturday") || text.contains("sunday")){
                val day = listOf("monday","tuesday","wednesday","thursday", "friday","saturday","sunday").find{text.contains(it)} ?: "monday"
                val msg = text.substringAfter(time, "reminder")
                return "#reminder set weekly $day $time $msg"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(Regex("\\b\\d{1,2}(st|nd|rd|th)?\\b").containsMatchIn(text)){
                val day = Regex("\\d{1,2}").find(text)?.value?.padStart(2, '0') ?: "01"
                val msg = text.substringAfter(time, "reminder")
                return "#reminder set monthly $day $time $msg"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("year") || text.contains("annually")){
                val date = Regex("(\\d{2})-(\\d{2})").find(text)?.value ?: "01-01"
                val msg = text.substringAfter(time, "reminder")
                return "#reminder set annually $date $time $msg"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("delete") || text.contains("cancel")){
                return "#reminder delete daily $time reminder"
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(text.contains("contact")){

            //START of IF-STATEMENT:
            if(text.contains("add")){
                val name = Regex("add (\\w+)").find(text)?.groupValues?.get(1) ?: "unknown"
                return "#contact add user $name"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("delete") || text.contains("remove")){
                val name = Regex("(delete|remove) (\\w+)").find(text)?.groupValues?.get(2) ?: "unknown"
                return "#contact delete user $name"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("email")){
                val email = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+").find(text)?.value ?: "unknown@email.com"
                return "#contact write user $email"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("ai")){
                val ai = Regex("ai (\\w+)").find(text)?.groupValues?.get(1) ?: "assistant"
                return "#contact add ai $ai"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(text.contains("group")){
                val group = Regex("group (\\w+)").find(text)?.groupValues?.get(1) ?: "team"
                return "#contact write group $group"
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        return "none"
    }//END of FUNCTION
}//END of CLASS: Translator