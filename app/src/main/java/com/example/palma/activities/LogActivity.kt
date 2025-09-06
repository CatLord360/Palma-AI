package com.example.palma.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.palma.databinding.ActivityLogBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

//START of CLASS: LogActivity
class LogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogBinding
    private val database = Firebase.database
    val auth = FirebaseAuth.getInstance()

    //START of  FUNCTION: onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ButtLog.setOnClickListener{
            validateCredentials()
        }

        binding.ButtSign.setOnClickListener{
            val intent = Intent(this@LogActivity, SignActivity::class.java)
            startActivity(intent)
        }
    }//END of FUNCTION: onCreate

    //START of FUNCTION: validateLog
    private fun validateCredentials(){
        val email = binding.InputEmail.text.toString()
        val password = binding.InputPassword.text.toString()

        //START of IF-STATEMENT:
        if(email.isNotBlank() && password.isNotBlank()){
            authenticateCredentials(email, password)
        }//END of IF-STATEMENT

        //START of ELSE-STATEMENT:
        else{
            val toast = Toast.makeText(this@LogActivity, "Logged In Unsuccessfully, incomplete credentials", Toast.LENGTH_SHORT)
            toast.show()
        }//END of ELSE-STATEMENT
    }//END of FUNCTION: validateCredentials

    // START of FUNCTION: authenticateCredentials
    private fun authenticateCredentials(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                //START of IF-STATEMENT:
                if(task.isSuccessful){
                    val reference = database.getReference("Palma/User")

                    reference.get().addOnSuccessListener { snapshot ->
                        var userKey: String? = null

                        //START of FOR-LOOP:
                        for(child in snapshot.children){
                            val personalInfo = child.child("Personal Information")
                            val storedEmail = personalInfo.child("email").getValue(String::class.java)

                            //START of IF-STATEMENT:
                            if(storedEmail == email) {
                                userKey = child.key
                                break
                            }//END of IF-STATEMENT
                        }//END of FOR-LOOP

                        //START of IF-STATEMENT:
                        if(userKey != null){
                            val contactReference = database.getReference("Palma/User/$userKey/Contact")

                            contactReference.get().addOnSuccessListener { contactSnapshot ->
                                var contactKey: String? = null
                                val first = contactSnapshot.children.firstOrNull()

                                //START of IF-STATEMENT:
                                if(first != null){
                                    contactKey = first.key
                                }//END of IF-STATEMENT

                                val toast = Toast.makeText(this@LogActivity, "Logged In Successfully", Toast.LENGTH_SHORT)
                                val intent = Intent(this@LogActivity, MessageActivity::class.java)
                                intent.putExtra("userKey", userKey)
                                intent.putExtra("contactKey", contactKey)
                                toast.show()
                                startActivity(intent)
                            }
                        }//END of IF-STATEMENT

                        //START of ELSE-STATEMENT
                        else{
                            val toast = Toast.makeText(this@LogActivity, "Logged In Unsuccessfully, user not found", Toast.LENGTH_SHORT)
                            toast.show()
                        }//END of ELSE-STATEMENT
                    }.addOnFailureListener { exception ->
                        val toast = Toast.makeText(this@LogActivity, "Logged In Unsuccessfully, ${exception.message}", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }//END of IF-STATEMENT

                //START of ELSE-STATEMENT:
                else {
                    val toast = Toast.makeText(this@LogActivity, "Logged In Unsuccessfully, ${task.exception?.message}", Toast.LENGTH_SHORT)
                    toast.show()
                }//END of ELSE-STATEMENT
            }
    }// END of FUNCTION
}//END of CLASS: LogActivity