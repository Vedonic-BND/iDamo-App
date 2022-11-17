package com.android.vedonic.idamo

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.vedonic.idamo.Adapter.MessageAdapter
import com.android.vedonic.idamo.databinding.ActivityChatBinding
import com.android.vedonic.idamo.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    var binding : ActivityChatBinding? = null
    var adapter : MessageAdapter?=null
    var messages :ArrayList<Message>? = null
    var senderRoom: String? = null
    var receiverRoom: String? = null
    var database: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog? = null
    var senderUid: String? = null
    var receiverUid: String? = null
    var name: String? = null
    var profile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.chatToolbar)

        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        dialog = ProgressDialog(this@ChatActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)

        messages = ArrayList()


        name = intent.getStringExtra("name")
        profile = intent.getStringExtra("image")


        receiverUid = intent.getStringExtra("uid")

        binding!!.userName.text = name.toString()
        Picasso.get().load(profile.toString()).placeholder(R.drawable.add_image_icon).into(binding!!.imageProfile)

        senderUid = FirebaseAuth.getInstance().uid

        database!!.collection("profile").document(receiverUid.toString()).collection("presence")
            .document(receiverUid.toString()).addSnapshotListener {snapshot, exception ->
                val status = snapshot?.getString("Presence")
                if (status == "Offline") {
                    binding!!.status.text = status.toString()
                    binding!!.status.visibility = View.VISIBLE
                }else{
                    binding!!.status.text = status.toString()
                    binding!!.status.visibility = View.VISIBLE
                }
            }

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        adapter = MessageAdapter(this@ChatActivity, messages  as ArrayList<Message>, senderRoom.toString(), receiverRoom.toString())


        messages?.clear()
        getMessage()


        binding!!.chatRecycler.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding!!.chatRecycler.adapter = adapter


        binding!!.sendBtn.setOnClickListener {
            binding!!.chatRecycler.postDelayed({
                binding!!.chatRecycler.scrollToPosition(binding!!.chatRecycler.adapter!!.itemCount - 1)
            }, 0)

            if (TextUtils.isEmpty(binding!!.messageBox.text.toString())) {
                Toast.makeText(this, "No comment added.", Toast.LENGTH_SHORT).show()
            }else{
                putMessage()
                binding!!.messageBox.setText("")
            }
        }

        binding!!.attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        }

        val handler = Handler()

        binding!!.messageBox.setOnClickListener {
            binding!!.chatRecycler.postDelayed({
                binding!!.chatRecycler.scrollToPosition(binding!!.chatRecycler.adapter!!.itemCount - 1)
            }, 0) }

        binding!!.messageBox.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                database!!.collection("profile").document(senderUid.toString()).collection("presence")
                    .document(senderUid.toString()).update("Presence", "Typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }
            var userStoppedTyping = Runnable {
                database!!.collection("profile").document(senderUid.toString()).collection("presence")
                    .document(senderUid.toString()).update("Presence", "Online")
            }
        })

        supportActionBar?.setDisplayShowTitleEnabled(false)

    }

    private fun putMessage() {
        val messageTxt:String = binding!!.messageBox.text.toString()
        val date = Date()
        //val message = Message(messageTxt,senderUid.toString(),date.time.toString())

        val messageID = date.time

        val messageMap = HashMap<String, Any>()
        messageMap["messageid"] = messageID.toString()
        messageMap["message"] = messageTxt
        messageMap["senderid"] = senderUid.toString()
        messageMap["timeStamp"] = date.time

        database!!.collection("chats").document(senderRoom.toString())
            .collection("message")
            .document(messageID.toString())
            .set(messageMap).addOnSuccessListener {
                database!!.collection("chats")
                    .document(receiverRoom.toString())
                    .collection("message")
                    .document(messageID.toString())
                    .set(messageMap).addOnSuccessListener {  }
            }

        val lastMsgObj = HashMap<String, Any>()
        lastMsgObj["lastMsg"] = messageTxt
        lastMsgObj["lastMsgTime"] = date.time

        database!!.collection("chats").document(senderRoom.toString()).set(lastMsgObj)
        database!!.collection("chats").document(receiverRoom.toString()).set(lastMsgObj)

        val currentID = FirebaseAuth.getInstance().uid
        val mesMap = HashMap<String, Any> ()
        mesMap["Messaged"] = "Yes"
        database!!.collection("profile").document(currentID.toString()).collection("messaged")
            .document(receiverUid.toString()).set(mesMap)
        database!!.collection("profile").document(receiverUid.toString()).collection("messaged")
            .document(currentID.toString()).set(mesMap)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMessage() {
        database!!.collection("chats").document(senderRoom.toString()).collection("message")
            .addSnapshotListener {snapshot, exception ->
                val message = snapshot!!.toObjects(Message::class.java)
                messages?.clear()
                messages?.addAll(message)
                adapter?.notifyDataSetChanged()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null){
                if (data.data != null){
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val ref = storage?.reference?.child("chats")?.child(calendar.timeInMillis.toString()+"")
                    dialog!!.show()
                    ref!!.putFile(selectedImage!!).addOnCompleteListener { task ->
                        dialog!!.dismiss()
                        if (task.isSuccessful){
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
                                val date = Date()

                                binding!!.messageBox.setText("")

                                val messageID = date.time

                                val messageImageMap = HashMap<String, Any>()
                                messageImageMap["messageid"] = messageID.toString()
                                messageImageMap["message"] = "photo"
                                messageImageMap["imageUrl"] = filePath
                                messageImageMap["senderid"] = senderUid.toString()
                                messageImageMap["timeStamp"] = date.time

                                database!!.collection("chats").document(senderRoom.toString())
                                    .collection("message")
                                    .document(messageID.toString())
                                    .set(messageImageMap).addOnSuccessListener {
                                        database!!.collection("chats")
                                            .document(receiverRoom.toString())
                                            .collection("message")
                                            .document(messageID.toString())
                                            .set(messageImageMap).addOnSuccessListener {  }
                                    }


                                val lastMsgObj = HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = "Photo"
                                lastMsgObj["lastMsgTime"] = date.time

                                database!!.collection("chats").document(senderRoom.toString()).set(lastMsgObj)
                                database!!.collection("chats").document(receiverRoom.toString()).set(lastMsgObj)

                                val currentID = FirebaseAuth.getInstance().uid
                                val mesMap = HashMap<String, Any> ()
                                database!!.collection("profile").document(currentID.toString()).collection("messaged")
                                    .document(receiverUid.toString()).set(mesMap)
                                database!!.collection("profile").document(receiverUid.toString()).collection("messaged")
                                    .document(currentID.toString()).set(mesMap)

                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentID = FirebaseAuth.getInstance().uid
        database!!.collection("profile").document(currentID.toString()).collection("presence")
            .document(currentID.toString()).update("Presence", "Online")

    }

    override fun onPause() {
        super.onPause()
        val currentID = FirebaseAuth.getInstance().uid
        database!!.collection("profile").document(currentID.toString()).collection("presence")
            .document(currentID.toString()).update("Presence", "Offline")

    }

    override fun onBackPressed() {
        val returnIntent = Intent(this, MyMessages::class.java)
        returnIntent.putExtra("receiverName", name)
        returnIntent.putExtra("receiverProfile", profile)
        returnIntent.putExtra("receiverUid", receiverUid)
        returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity( returnIntent)
        finish()
    }
}