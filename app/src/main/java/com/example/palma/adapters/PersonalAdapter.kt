package com.example.palma.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.palma.R
import com.example.palma.models.Contact

//START of CLASS: PersonalAdapter
class PersonalAdapter(private val context: Context, private val mList: List<Contact>): RecyclerView.Adapter<PersonalAdapter.ViewHolder>(){
    //START of FUNCTION: onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_personal, parent, false)

        return ViewHolder(view)
    }//END of FUNCTION: onCreateViewHolder

    //START of FUNCTION: onBindViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val contact = mList[position]

        holder.username.text = contact.username.toString()
        holder.mobile.text = contact.mobile.toString()
        holder.email.text = contact.email.toString()

        //START of IF-STATEMENT:
        if(contact.type == "ai"){
            loadAI(holder, position)
        }//END of IF-STATEMENT
    }//END of FUNCTION: onBindViewHolder

    //START of FUNCTION: getItemCount
    override fun getItemCount(): Int {
        return mList.size
    }//END of FUNCTION: getItemCount

    //START of FUNCTION: loadAI
    private fun loadAI(holder: ViewHolder, position: Int){
        val contact = mList[position]

        //START of IF-STATEMENT:
        if(contact.username == "Palma"){
            holder.image.setImageResource(R.drawable.ic_palma)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.primary))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Tom"){
            holder.image.setImageResource(R.drawable.ic_tom)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.purple))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Index"){
            holder.image.setImageResource(R.drawable.ic_index)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Mid"){
            holder.image.setImageResource(R.drawable.ic_mid)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Rin"){
            holder.image.setImageResource(R.drawable.ic_rin)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(contact.username == "Pinky"){
            holder.image.setImageResource(R.drawable.ic_pinky)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.pink))
        }//END of IF-STATEMENT
    }//END of FUNCTION: loadAI

    //START of CLASS: ViewHolder
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val layout: RelativeLayout = view.findViewById<RelativeLayout>(R.id.Layout_Cardview)
        val image: ImageView = view.findViewById<ImageView>(R.id.Image_Contact)
        val username: TextView = view.findViewById<TextView>(R.id.Output_Username)
        val mobile: TextView = view.findViewById<TextView>(R.id.Output_Mobile)
        val email: TextView = view.findViewById<TextView>(R.id.Output_Email)
    }//END of CLASS: ViewHolder
}//END of CLASS: PersonalAdapter