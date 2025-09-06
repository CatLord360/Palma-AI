package com.example.palma.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palma.adapters.PersonalAdapter
import com.example.palma.databinding.ActivityContactBinding
import com.example.palma.models.Contact
import com.google.firebase.Firebase
import com.google.firebase.database.database

//START of CLASS: ContactActivity
class ContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    private lateinit var adapter: PersonalAdapter
    private var list = ArrayList<Contact>()
    private val database = Firebase.database

    //START of FUNCTION: onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userKey = intent.getStringExtra("userKey")
        val contactKey = intent.getStringExtra("contactKey")

        //START of IF-STATEMENT:
        if(userKey != null && contactKey != null){
            setupRecyclerView()
            displayContact(userKey, contactKey)
        }//END of IF-STATEMENT
    }//END of FUNCTION: onCreate

    //START of FUNCTION: setupRecyclerView
    private fun setupRecyclerView(){
        adapter = PersonalAdapter(this@ContactActivity, list)
        binding.ListContact.adapter = adapter
        binding.ListContact.layoutManager = LinearLayoutManager(this@ContactActivity)
    }//END of FUNCTION: setupRecyclerView

    //START of FUNCTION: displayContact
    private fun displayContact(userKey: String, contactKey: String){
        val reference = database.getReference("Palma/User/$userKey/Contact/$contactKey")
        val groupReference = database.getReference("Palma/User/$userKey/Contact/$contactKey/Member")

        reference.get().addOnSuccessListener{ snapshot ->
            val contact = snapshot.getValue(Contact::class.java)

            //START of IF-STATEMENT:
            if(contact != null){
                //START of IF-STATEMENT:
                if(contact.type == "ai" || contact.type == "user"){
                    list.add(contact)
                }//END of IF-STATEMENT

                //START of IF-STATEMENT:
                if(contact.type == "group"){
                    groupReference.get().addOnSuccessListener { groupSnapshot ->
                        for(child in groupSnapshot.children) {
                            val username = child.child("username").getValue(String::class.java) ?: ""
                            val mobile = child.child("mobile").getValue(String::class.java) ?: ""
                            val email = child.child("email").getValue(String::class.java) ?: ""
                            val type = child.child("type").getValue(String::class.java) ?: ""

                            val member = Contact("", username, mobile, email, type)
                            list.add(member)
                        }

                        adapter?.notifyDataSetChanged()
                    }
                }//END of IF-STATEMENT
            }//END of IF-STATEMENT
        }
    }//END of FUNCTION: displayContact
}//END of CLASS: ContactActivity