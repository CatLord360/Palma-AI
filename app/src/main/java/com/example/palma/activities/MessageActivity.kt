package com.example.palma.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palma.R
import com.example.palma.adapters.ContactAdapter
import com.example.palma.adapters.MessageAdapter
import com.example.palma.ai.AI
import com.example.palma.databinding.ActivityMessageBinding
import com.example.palma.models.Contact
import com.example.palma.models.Message
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.tasks.await

//START of CLASS: MessageActivity
class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var messageAdapter: MessageAdapter
    private var contactList = ArrayList<Contact>()
    private var messageList = ArrayList<Message>()
    private val database = Firebase.database

    //START of FUNCTION: onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userKey = intent.getStringExtra("userKey")
        val contactKey = intent.getStringExtra("contactKey")

        //START of IF-STATEMENT:
        if(userKey != null && contactKey != null){
            setupRecyclerView(userKey)
            loadContact(userKey)
            loadMessage(userKey, contactKey)

            binding.ImageContact.setOnClickListener{
                val intent = Intent(this@MessageActivity, ContactActivity::class.java)
                intent.putExtra("userKey", userKey)
                intent.putExtra("contactKey", contactKey)
                startActivity(intent)
            }

            binding.ButtSend.setOnClickListener{
                writeMessage(userKey, contactKey)
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: onCreate

    //START of FUNCTION: setupRecyclerView
    private fun setupRecyclerView(userKey: String){
        contactAdapter = ContactAdapter(this@MessageActivity, userKey, contactList)
        binding.ListContact.adapter = contactAdapter
        binding.ListContact.layoutManager = LinearLayoutManager(this@MessageActivity)

        messageAdapter = MessageAdapter(this@MessageActivity, userKey, messageList)
        binding.ListMessage.adapter = messageAdapter
        binding.ListMessage.layoutManager = LinearLayoutManager(this@MessageActivity)
        binding.ListMessage.scrollToPosition(messageAdapter.itemCount - 1)
    }//END of FUNCTION: setupRecyclerView

    //START of FUNCTION: loadContact
    private fun loadContact(userKey: String) {
        val reference = database.getReference("Palma/User/$userKey/Contact")

        reference.addValueEventListener(object: ValueEventListener{
            //START of FUNCTION: onDataChange
            override fun onDataChange(snapshot: DataSnapshot){
                contactList.clear()

                //START of FOR-LOOP:
                for(contactSnapshot in snapshot.children){
                    val contact = contactSnapshot.getValue(Contact::class.java)

                    //START of IF-STATEMENT:
                    if(contact != null){
                        contactList.add(contact)
                    }//END of IF-STATEMENT
                }//END of FOR-LOOP

                contactAdapter.notifyDataSetChanged()
            }//END of FUNCTION: onDataChange

            //START of FUNCTION: onCancelled
            override fun onCancelled(error: DatabaseError){
            }//END of FUNCTION: onCancelled
        })
    }//END of FUNCTION: loadContact

    //START of FUNCTION: loadMessage
    private fun loadMessage(userKey: String, contactKey: String) {
        val reference = database.getReference("Palma/User/$userKey/Contact/$contactKey")

        reference.get().addOnSuccessListener{ snapshot ->
            binding.OutputContact.text = snapshot.child("username").getValue(String::class.java).toString()
            val messageKey = snapshot.child("messageKey").getValue(String::class.java).toString()
            val messageReference = database.getReference("Palma/Message/$messageKey")

            //START of IF-STATEMENT:
            if(snapshot.child("type").getValue(String::class.java).toString() == "ai"){
                loadAI(snapshot.child("username").getValue(String::class.java).toString())
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(snapshot.child("type").getValue(String::class.java).toString() == "group"){
                binding.ImageContact.setImageResource(R.drawable.ic_group)
            }//END of IF-STATEMENT

            messageReference.addValueEventListener(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    messageList.clear()
                    var index = 1

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild("message$index")){
                        val messageSnapshot = snapshot.child("message$index")
                        val message = messageSnapshot.getValue(Message::class.java)

                        //START of IF-STATEMENT:
                        if (message == null){
                            index++
                            continue
                        }//END of IF-STATEMENT

                        messageList.add(message)
                        index++
                    }//WHILE of FOR-LOOP

                    messageAdapter.notifyDataSetChanged()
                    binding.ListMessage.scrollToPosition(messageAdapter.itemCount - 1)
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: loadMessage

    //START of FUNCTION: loadAI
    private fun loadAI(ai: String){
        //START of IF-STATEMENT:
        if(ai == "Palma"){
            binding.ImageContact.setImageResource(R.drawable.ic_palma)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(ai == "Tom"){
            binding.ImageContact.setImageResource(R.drawable.ic_tom)
            binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.purple))
            binding.LayoutSend.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.purple))
            binding.InputMessage.setHintTextColor(ContextCompat.getColor(this@MessageActivity, R.color.purple))
            binding.InputMessage.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.purple))
            binding.ButtSend.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.purple))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(ai == "Index"){
            binding.ImageContact.setImageResource(R.drawable.ic_index)
            binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.blue))
            binding.LayoutSend.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.blue))
            binding.InputMessage.setHintTextColor(ContextCompat.getColor(this@MessageActivity, R.color.blue))
            binding.InputMessage.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.blue))
            binding.ButtSend.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.blue))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(ai == "Mid"){
            binding.ImageContact.setImageResource(R.drawable.ic_mid)
            binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.orange))
            binding.LayoutSend.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.orange))
            binding.InputMessage.setHintTextColor(ContextCompat.getColor(this@MessageActivity, R.color.orange))
            binding.InputMessage.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.orange))
            binding.ButtSend.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.orange))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(ai == "Rin"){
            binding.ImageContact.setImageResource(R.drawable.ic_rin)
            binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.red))
            binding.LayoutSend.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.red))
            binding.InputMessage.setHintTextColor(ContextCompat.getColor(this@MessageActivity, R.color.red))
            binding.InputMessage.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.red))
            binding.ButtSend.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.red))
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(ai == "Pinky"){
            binding.ImageContact.setImageResource(R.drawable.ic_pinky)
            binding.LayoutHeader.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.pink))
            binding.LayoutSend.setBackgroundColor(ContextCompat.getColor(this@MessageActivity, R.color.pink))
            binding.InputMessage.setHintTextColor(ContextCompat.getColor(this@MessageActivity, R.color.pink))
            binding.InputMessage.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.pink))
            binding.ButtSend.setTextColor(ContextCompat.getColor(this@MessageActivity, R.color.pink))
        }//END of IF-STATEMENT
    }//END of FUNCTION: loadAI

    //START of FUNCTION: writeMessage
    private fun writeMessage(userKey: String, contactKey: String){
        val reference = database.getReference("Palma/User/$userKey/Contact/$contactKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        reference.get().addOnSuccessListener{ snapshot ->
            val messageKey = snapshot.child("messageKey").getValue(String::class.java).toString()
            val type = snapshot.child("type").getValue(String::class.java).toString()
            val username = snapshot.child("username").getValue(String::class.java).toString()
            val messageReference = database.getReference("Palma/Message/$messageKey")

            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    var index = 1
                    var key = "message$index"
                    val message = binding.InputMessage.text.toString()

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild(key)){
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    messageReference.child(key).setValue(Message(userKey, date, time, message))

                    //START of IF-STATEMENT:
                    if(type == "ai"){
                        AI().writeAI(userKey, messageKey, username, message)
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "group"){
                        CoroutineScope(Dispatchers.IO).launch{
                            try{
                                val memberSnapshot = reference.child("Member").get().await()

                                //START of FOR-LOOP:
                                for(member in memberSnapshot.children){
                                    //START of IF-STATEMENT:
                                    if(member.child("type").getValue(String::class.java) == "ai"){
                                        delay(600)
                                        AI().writeAI(userKey, messageKey, member.child("username").getValue(String::class.java).toString(), message)
                                    }//END of IF-STATEMENT
                                }//END of FOR-LOOP
                            } catch (e: Exception) {
                                Log.e("writeMessage", "Error handling group AI: ${e.message}")
                            }
                        }
                    }//END of IF-STATEMENT
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: writeMessage
}//END of CLASS: MessageActivity