package com.example.palma.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.palma.R
import com.example.palma.activities.MessageActivity
import com.example.palma.models.Contact
import com.google.android.material.textview.MaterialTextView
import android.content.Context
import androidx.core.content.ContextCompat

//START of CLASS: ContactAdapter
class ContactAdapter(private val context: Context, private val userKey: String, private val mList: List<Contact>): RecyclerView.Adapter<ContactAdapter.ViewHolder>(){
    //START of FUNCTION: onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_contact, parent, false)

        return ViewHolder(view)
    }//END of FUNCTION: onCreateViewHolder

    //START of FUNCTION: onBindViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val contact = mList[position]
        val index = position + 1

        holder.contactOutput.text = contact.username.toString()

        //START of IF-STATEMENT:
        if(contact.type == "ai"){
            displayAI(holder, position)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.type == "group"){
            holder.contactImage.setImageResource(R.drawable.ic_group)
        }//END of IF-STATEMENT

        holder.contactImage.setOnClickListener{
            val intent = Intent(context, MessageActivity::class.java)
            val contactKey = "Contact - $index"
            intent.putExtra("userKey", userKey)
            intent.putExtra("contactKey", contactKey)

            context.startActivity(intent)
        }
    }//END of FUNCTION: onBindViewHolder

    //START of FUNCTION: getItemCount
    override fun getItemCount(): Int{
        return mList.size
    }//END of FUNCTION: getItemCount

    //START of FUNCTION: displayAI
    private fun displayAI(holder: ViewHolder, position: Int){
        val contact = mList[position]

        //START of IF-STATEMENT:
        if(contact.username == "Palma"){
            holder.contactImage.setImageResource(R.drawable.ic_palma)
            holder.contactOutput.setTextColor(ContextCompat.getColor(context, R.color.green))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Tom"){
            holder.contactImage.setImageResource(R.drawable.ic_tom)
            holder.contactOutput.setTextColor(ContextCompat.getColor(context, R.color.purple))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Index"){
            holder.contactImage.setImageResource(R.drawable.ic_index)
            holder.contactOutput.setTextColor(ContextCompat.getColor(context, R.color.blue))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Mid"){
            holder.contactImage.setImageResource(R.drawable.ic_mid)
            holder.contactOutput.setTextColor(ContextCompat.getColor(context, R.color.orange))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Rin"){
            holder.contactImage.setImageResource(R.drawable.ic_rin)
            holder.contactOutput.setTextColor(ContextCompat.getColor(context, R.color.red))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Pinky"){
            holder.contactImage.setImageResource(R.drawable.ic_pinky)
            holder.contactOutput.setTextColor(ContextCompat.getColor(context, R.color.pink))
        }//END of IF-STATEMENT
    }//END of FUNCTION: displayAI

    //START of CLASS: ViewHolder
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val contactImage: ImageView = view.findViewById<ImageView>(R.id.Image_Contact)
        val contactOutput: MaterialTextView = view.findViewById(R.id.Output_Contact)
    }//END of CLASS: ViewHolder
}//END of CLASS: ContactAdapter