package com.example.palma.ai.mid

//START of CLASS: Command
class Command{
    //START of FUNCTION: writeCommand:
    fun writeCommand(userKey: String, messageKey: String, message: String){
        //START of IF-STATEMENT
        if(message.lowercase().trim().startsWith("#list")){
            List().writeList(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT
        if(message.lowercase().trim().startsWith("#compute")){
            Compute().writeCompute(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeCommand
}//END of CLASS: Command