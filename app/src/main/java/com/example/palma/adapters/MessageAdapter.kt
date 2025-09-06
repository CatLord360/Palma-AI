package com.example.palma.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.palma.R
import com.example.palma.dialog.Dialog
import com.example.palma.models.Message

//START of CLASS: MessageAdapter
class MessageAdapter(private val context: Context, private val userKey: String, private val mList: List<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    //START of FUNCTION: onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_message, parent, false)

        return ViewHolder(view)
    }//END of FUNCTION: onCreateViewHolder

    //START of FUNCTION: onBindViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = mList[position]

        holder.messageOutput.text = message.message.toString()

        val layoutParams = holder.messageOutput.layoutParams as RelativeLayout.LayoutParams
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START)
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END)

        //START of IF-STATEMENT:
        if(message.userKey.toString() == "AI - 2" || message.userKey.toString() == "AI - 3" || message.userKey.toString() == "AI - 4" || message.userKey.toString() == "AI - 5" || message.userKey.toString() == "AI - 6"){
            loadAI(holder, position)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(userKey == message.userKey){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        }//END of IF-STATEMENT

        holder.messageOutput.layoutParams = layoutParams

        holder.messageOutput.setOnClickListener{
            Dialog(context).loadMessage(message)
        }
    }//END of FUNCTION: onBindViewHolder

    //START of FUNCTION: getItemCount
    override fun getItemCount(): Int {
        return mList.size
    }//END of FUNCTION: getItemCount

    //START of FUNCTION: loadAI
    private fun loadAI(holder: ViewHolder, position: Int){
        val message = mList[position]

        //START of IF-STATEMENT:
        if(message.userKey == "AI - 2"){
            holder.messageOutput.backgroundTintList = ContextCompat.getColorStateList(context, R.color.purple)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.userKey == "AI - 3"){
            holder.messageOutput.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.userKey == "AI - 4"){
            holder.messageOutput.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.userKey == "AI - 5"){
            holder.messageOutput.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.userKey == "AI - 6"){
            holder.messageOutput.backgroundTintList = ContextCompat.getColorStateList(context, R.color.pink)
        }//END of IF-STATEMENT
    }//END of FUNCTION: loadAI

    //START of CLASS: ViewHolder
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val messageOutput: Button = view.findViewById(R.id.Output_Message)
    }//END of CLASS: ViewHolder
}//END of CLASS: MessageAdapter