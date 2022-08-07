package com.psnltracker

import android.content.Context
import android.content.SharedPreferences

class PrefrenceHelper{
    companion object{
        val islogedIn:String="is_logedIn"
        val sharePrefName:String="SharePrefName"
        val sessionStartTime:String="SessionTime"
        val isScreenLocked:String="ScreenLocked"
        val userToken:String="user_token"
        val mobileNumber:String="mobileNumber"
        val outletId:String="outlet_id"

        fun getToken(context: Context):String{
           val prefrence=context.getSharedPreferences(sharePrefName,Context.MODE_PRIVATE)
            val token=prefrence.getString(userToken,"")
            return token!!
        }

        fun getSavedValue(context: Context,key:String):String{
            val prefrence=context.getSharedPreferences(sharePrefName,Context.MODE_PRIVATE)
            val token=prefrence.getString(key,"")
            return token!!
        }

        fun setStringValue(context: Context,key:String,value:String){
            val prefrence=context.getSharedPreferences(sharePrefName,Context.MODE_PRIVATE)
            val editor = prefrence.edit()
            editor.putString(key, value)
            editor.apply()
            editor.commit()
        }
    }
}