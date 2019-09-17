package com.hossam.hasanin.getadate.Externals

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.Builder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.R
import java.util.*
import kotlin.collections.ArrayList

class mFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var notificationManager:NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val mAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (mAuth.currentUser!!.uid != remoteMessage.data["id"]){

            if (remoteMessage.data["noti_type"].equals("newUser")) {
                val location = remoteMessage.data["location"]?.split(",")?.map {
                    it.toDouble()
                }
                val user = User(
                    username = remoteMessage.data["username"],
                    id = remoteMessage.data["id"],
                    age = remoteMessage.data["age"]?.toInt(),
                    email = remoteMessage.data["email"],
                    gender = remoteMessage.data["gender"]?.toInt(),
                    online = remoteMessage.data["online"]?.toBoolean(),
                    location = location as ArrayList<Double?>
                )

                newUserListener.postValue(user)

            }

            //Setting up Notification channels for android O and above
            //Setting up Notification channels for android O and above
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                setupChannels()
            }

            val notificationId = Random().nextInt(60000)

            Log.v("Notification", remoteMessage.notification!!.title)

            val defaultSoundUri: Uri? =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: Builder =
                Builder(
                    this,
                    Constants.NOTIFICATION_CHANALE_ID.toString()
                )
                    .setSmallIcon(R.drawable.main_logo)
                    .setContentTitle(remoteMessage.notification!!.title) //the "title" value you sent in your notification
                    .setContentText(remoteMessage.notification!!.body) //ditto
                    .setAutoCancel(true)  //dismisses the notification on click
                    .setSound(defaultSoundUri)

            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build())

        }
        Log.v("koko" , "noti")

    }

    @RequiresApi(api = VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName: CharSequence = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANALE_ID.toString() ,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        saveUserToken(token)
    }

    private fun saveUserToken(token: String){
        val firestore = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        firestore.collection("users").document(mAuth.currentUser!!.uid)
            .update("token_id" , token)
    }

}