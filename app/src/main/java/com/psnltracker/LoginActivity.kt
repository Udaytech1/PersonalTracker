package com.psnltracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var editText : EditText
    private lateinit var loginButton : MaterialButton
    private val URL ="https://prsntracker-default-rtdb.firebaseio.com/"
    private var mobileNumber= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
    }

    private fun initView() {
        editText = findViewById(R.id.editNumber)
        loginButton = findViewById(R.id.loginButton)

        val mobileumber = PrefrenceHelper.getSavedValue(this,PrefrenceHelper.mobileNumber)

        if (!mobileumber.equals("")){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            mobileNumber = editText.text.toString().trim()
            if (mobileNumber.length>=10){
                saveData()
            }else{
                editText.setError("Fill it")
                editText.requestFocus()
                Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("$URL")
        val myRef: DatabaseReference = database.getReference("Devices").child("${mobileNumber}")
        val json = JSONObject()
        json.put("Brand Name",Build.BRAND)
        json.put("Device",Build.DEVICE)
        json.put("Model",Build.MODEL)
        json.put("Login Time",Build.TIME)
        myRef.setValue("$json")
        PrefrenceHelper.setStringValue(this,PrefrenceHelper.mobileNumber,"$mobileNumber")
        startActivity(Intent(this,MainActivity::class.java))
    }
}