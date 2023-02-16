package com.android.iDamoTeam.idamo.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.iDamoTeam.idamo.R
import com.android.iDamoTeam.idamo.databinding.ReceiveMsgItemBinding
import com.android.iDamoTeam.idamo.databinding.SendMsgItemBinding
import com.android.iDamoTeam.idamo.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageAdapter(var mContext: Context,
                     mMessage: List<Message>,
                     senderRoom: String,
                     receiverRoom:String ) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    lateinit var mMessage: ArrayList<Message>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2
    val senderRoom: String
    var receiverRoom: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.send_msg_item, parent, false)
            SentMsgHolder(view)

        }else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.receive_msg_item , parent, false)
            ReceiveMsgHolder(view)
        }
    }


    override fun getItemViewType(position: Int): Int {
        val messages = mMessage[position]
        return if (FirebaseAuth.getInstance().uid == messages.senderid) {
            ITEM_SENT
        }else{
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessage[position]
        holder.setIsRecyclable(false)

        if (holder.javaClass == SentMsgHolder::class.java){
            val viewHolder = holder as SentMsgHolder
            if (message.message == "photo"){
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.messages.visibility = View.GONE
                viewHolder.binding.mlinear.visibility = View.GONE
                Picasso.get().load(message.imageUrl).placeholder(R.drawable.add_image_icon).into(viewHolder.binding.image)
            }
            viewHolder.binding.messages.text = message.message
        }else{
            val viewHolder = holder as ReceiveMsgHolder
            if (message.message == "photo"){
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.messages.visibility = View.GONE
                viewHolder.binding.mlinear.visibility = View.GONE
                Picasso.get().load(message.imageUrl).placeholder(R.drawable.add_image_icon).into(viewHolder.binding.image)
            }
            viewHolder.binding.messages.text = message.message
        }
    }

    override fun getItemCount(): Int = mMessage.size

    inner class SentMsgHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var binding: SendMsgItemBinding = SendMsgItemBinding.bind(itemView)
    }

    inner class ReceiveMsgHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var binding: ReceiveMsgItemBinding = ReceiveMsgItemBinding.bind(itemView)
    }

    init {
        if (mMessage != null) {
            this.mMessage = mMessage as ArrayList<Message>
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }




}