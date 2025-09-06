package com.example.palma.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.palma.ai.index.Index
import com.example.palma.ai.mid.Mid
import com.example.palma.ai.palma.Palma
import com.example.palma.ai.pinky.Pinky
import com.example.palma.ai.rin.Rin
import com.example.palma.ai.tom.Tom
import com.example.palma.databinding.ActivitySignBinding
import com.example.palma.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

//START of CLASS: SignActivity
class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private val database = Firebase.database
    val auth = FirebaseAuth.getInstance()

    //START of FUNCTION: onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.InputBirthdate.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.InputBirthdate.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }

        binding.ButtSign.setOnClickListener{
            validateCredentials()
        }
    }//END of FUNCTION: onCreate

    //START of FUNCTION: validateCredentials
    private fun validateCredentials(){
        val username = binding.InputUsername.text.toString().trim()
        val gender = binding.InputGender.selectedItem.toString()
        val birthdate = binding.InputBirthdate.text.toString()
        val mobile = binding.InputMobile.text.toString().trim()
        val email = binding.InputEmail.text.toString().trim()
        val password = binding.InputPassword.text.toString().trim()
        val repeat = binding.InputRepeat.text.toString().trim()

        //START of IF-STATEMENT:
        if(username.isNotBlank() && gender.isNotBlank() && birthdate.isNotBlank() && mobile.isNotBlank() && email.isNotBlank() && password.isNotBlank() && repeat.isNotBlank()){
            //START of IF-STATEMENT:
            if(password == repeat){
                writeUser(username, gender, birthdate, mobile, email, password)
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        //START of ELSE-STATEMENT:
        else{
            val toast = Toast.makeText(this@SignActivity, "Signed In Unsuccessfully, incomplete credentials", Toast.LENGTH_SHORT)
            toast.show()
        }//END of ELSE-STATEMENT
    }//END of FUNCTION: validateCredentials

    //START of FUNCTION: writeUser
    private fun writeUser(username: String, gender: String, birthdate: String, mobile: String, email: String, password: String) {
        val reference = database.getReference("Palma/User")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                //START of IF-STATEMENT
                if(task.isSuccessful){
                    var index = 1
                    var userKey = "User - $index"

                    reference.get().addOnSuccessListener { snapshot ->
                        //START of WHILE-LOOP
                        while (snapshot.hasChild(userKey)) {
                            index++
                            userKey = "User - $index"
                        }

                        val user = User(username, gender, birthdate, mobile, email, password)

                        reference.child(userKey).child("Personal Information").setValue(user)
                            .addOnCompleteListener {
                                writeAI(userKey, username)
                            }
                    }
                }//END of IF-STATEMENT

                //START of ELSE-STATEMENT:
                else{
                    val toast = Toast.makeText(this@SignActivity, "Sign In Failed: ${task.exception?.message}", Toast.LENGTH_SHORT)
                    toast.show()
                }//END of ELSE-STATEMENT
            }
    }//END of FUNCTION: writeUser

    //START of FUNCTION: writeAI
    private fun writeAI(userKey: String, username: String){
        CoroutineScope(Dispatchers.Main).launch {
            //START of TRY:
            try{
                Palma().writePalma(userKey, username).await()
                Tom().writeTom(userKey, username).await()
                Index().writeIndex(userKey, username).await()
                Mid().writeMid(userKey, username).await()
                Rin().writeRin(userKey, username).await()
                Pinky().writePinky(userKey, username).await()

                val toast = Toast.makeText(this@SignActivity, "Signed In Successfully", Toast.LENGTH_SHORT)
                val intent = Intent(this@SignActivity, MessageActivity::class.java)
                intent.putExtra("userKey", userKey)
                intent.putExtra("contactKey", "Contact - 1")
                toast.show()
                startActivity(intent)
            }//END of TRY

            //START of CATCH:
            catch (e: Exception){
                val toast = Toast.makeText(this@SignActivity, "Signed In Unsuccessfully, ${e.message}", Toast.LENGTH_SHORT)
                toast.show()
            }//END of CATCH
        }
    }//END of FUNCTION
}//END of CLASS: SignActivity