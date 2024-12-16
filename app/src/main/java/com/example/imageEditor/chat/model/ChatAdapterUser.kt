package com.example.imageEditor.chat.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapterUser(
    val listUser: MutableList<UserMessageModel>,
) : RecyclerView.Adapter<ChatAdapterUser.NoteViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(com.example.imageEditor.R.layout.view_chat_layout_user, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bindView(listUser[position].user, listUser[position].message)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentView: ConstraintLayout = itemView.findViewById(com.example.imageEditor.R.id.user_parent_view)
        val nameOfUser: TextView = itemView.findViewById(com.example.imageEditor.R.id.nameOfUser1)
        val statusOfUser: CircleImageView = itemView.findViewById(com.example.imageEditor.R.id.viewStatus)
        val avatar: CircleImageView = itemView.findViewById(com.example.imageEditor.R.id.cardviewOfUser1)
        val lastMessage: TextView = itemView.findViewById(com.example.imageEditor.R.id.statusOfUser)


        fun bindView(user: User, message: Message) {
            Glide.with(parentView.context).load(user.image).into(avatar)
            if (message.type == 0) {
                if (message.senderId == FirebaseAuth.getInstance().uid) {
                    lastMessage.text = "Bạn: " + message.message
                } else {
                    lastMessage.text = message.message
                }
            } else {
                if (message.senderId == FirebaseAuth.getInstance().uid) {
                    lastMessage.text = if (message.type == 1) "Bạn: Hình ảnh" else "Bạn: Video"
                } else {
                    lastMessage.text = if (message.type == 1) "Hình ảnh" else "Video"
                }
            }
            nameOfUser.text = user.name
            if (user.status == "online") {
                statusOfUser.visibility = View.VISIBLE
            } else {
                statusOfUser.visibility = View.INVISIBLE
            }

            parentView.setOnClickListener(View.OnClickListener { v ->
                val intent = Intent(v.context, SpecificChat::class.java)
                intent.putExtra("name", user.name)
                intent.putExtra("receiveruid", user.id)
                intent.putExtra("imageuri", user.image)
                intent.putExtra("publicKey", user.publicKey)
                v.context.startActivity(intent)
            })
        }
    }

}