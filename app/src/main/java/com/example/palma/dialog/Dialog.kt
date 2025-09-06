package com.example.palma.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.palma.R
import com.example.palma.databinding.DialogMessageBinding
import com.example.palma.models.Message
import com.example.palma.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

//START of CLASS: Dialog
class Dialog(context: Context): AlertDialog.Builder(context){
    private val database = Firebase.database

    //START of FUNCTION: loadMessage
    @SuppressLint("SetTextI18n")
    fun loadMessage(message: Message) {
        val userReference = database.getReference("Palma/User/${message.userKey}/Personal Information")
        val binding = DialogMessageBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        val dialog = builder.create()

        //START of IF-STATEMENT:
        if(message.userKey.toString().trim().startsWith("User")){
            userReference.addValueEventListener(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val username = snapshot.child("username").getValue(String::class.java).toString()
                    binding.OutputUsername.text = username
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.userKey.toString().trim().startsWith("AI")){
            //START of IF-STATEMENT:
            if(message.userKey == "AI - 1"){
                binding.ImageContact.setImageResource(R.drawable.ic_palma)
                binding.OutputUsername.text = "Palma"
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(message.userKey == "AI - 2"){
                binding.ImageContact.setImageResource(R.drawable.ic_tom)
                binding.OutputUsername.text = "Tom"
                binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.purple))
                binding.LabelDate.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.OutputDate.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.LabelTime.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.OutputTime.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.LabelMessage.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.OutputMessage.setTextColor(ContextCompat.getColor(context, R.color.purple))
                binding.ButtExit.backgroundTintList = ContextCompat.getColorStateList(context, R.color.purple)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(message.userKey == "AI - 3"){
                binding.ImageContact.setImageResource(R.drawable.ic_index)
                binding.OutputUsername.text = "Index"
                binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                binding.LabelDate.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.OutputDate.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.LabelTime.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.OutputTime.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.LabelMessage.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.OutputMessage.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.ButtExit.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(message.userKey == "AI - 4"){
                binding.ImageContact.setImageResource(R.drawable.ic_mid)
                binding.OutputUsername.text = "Mid"
                binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
                binding.LabelDate.setTextColor(ContextCompat.getColor(context, R.color.orange))
                binding.OutputDate.setTextColor(ContextCompat.getColor(context, R.color.orange))
                binding.LabelTime.setTextColor(ContextCompat.getColor(context, R.color.orange))
                binding.OutputTime.setTextColor(ContextCompat.getColor(context, R.color.orange))
                binding.LabelMessage.setTextColor(ContextCompat.getColor(context, R.color.orange))
                binding.OutputMessage.setTextColor(ContextCompat.getColor(context, R.color.orange))
                binding.ButtExit.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(message.userKey == "AI - 5"){
                binding.ImageContact.setImageResource(R.drawable.ic_rin)
                binding.OutputUsername.text = "Rin"
                binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                binding.LabelDate.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.OutputDate.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.LabelTime.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.OutputTime.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.LabelMessage.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.OutputMessage.setTextColor(ContextCompat.getColor(context, R.color.red))
                binding.ButtExit.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(message.userKey == "AI - 6"){
                binding.ImageContact.setImageResource(R.drawable.ic_pinky)
                binding.OutputUsername.text = "Pinky"
                binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.pink))
                binding.LabelDate.setTextColor(ContextCompat.getColor(context, R.color.pink))
                binding.OutputDate.setTextColor(ContextCompat.getColor(context, R.color.pink))
                binding.LabelTime.setTextColor(ContextCompat.getColor(context, R.color.pink))
                binding.OutputTime.setTextColor(ContextCompat.getColor(context, R.color.pink))
                binding.LabelMessage.setTextColor(ContextCompat.getColor(context, R.color.pink))
                binding.OutputMessage.setTextColor(ContextCompat.getColor(context, R.color.pink))
                binding.ButtExit.backgroundTintList = ContextCompat.getColorStateList(context, R.color.pink)
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        binding.OutputDate.text = message.date.toString()
        binding.OutputTime.text = message.time.toString()
        binding.OutputMessage.text = message.message.toString()

        binding.ButtExit.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }//END of FUNCTION: loadMessage
}//END of CLASS: Dialog