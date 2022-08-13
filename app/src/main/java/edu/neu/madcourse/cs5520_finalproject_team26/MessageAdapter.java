package edu.neu.madcourse.cs5520_finalproject_team26;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.neu.madcourse.cs5520_finalproject_team26.models.Message;
import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class MessageAdapter extends RecyclerView.Adapter< MessageAdapter.messagesViewholder> {

    Context context;
    ArrayList<Message> messagesList;
    ArrayList<User> usersList;
    MessagesInterface messageListener;

    public MessageAdapter(Context context, MessagesInterface msgListener,ArrayList<Message> messagesList,ArrayList<User> usersList) {
        this.context = context;
        this.messagesList = messagesList;
        this.usersList = usersList;
        this.messageListener = msgListener;
    }


    @Override
    public void onBindViewHolder(@NonNull messagesViewholder holder, int position) {
            Message message = messagesList.get(getItemCount() - position -1);
            holder.senderName.setText(getSenderName(message.getSenderId()));
            holder.messageText.setText(message.getMessageText());
            holder.messageLocation.setText(message.getLocation());
            holder.messageTime.setText(message.getSentTime());
            if(!message.isSeen()){
                holder.itemView.setBackgroundColor(Color.parseColor("#FBEF6F"));
            }
            Picasso.get().load(getUserProfilePic(message.getSenderId())).into(holder.userProfilePic);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageListener.onItemClicked(messagesList.get(getItemCount() - position-1));
                }
            });
    }

    private String getSenderName(String senderId) {
        for(User u : usersList){
            if(u.getUserId().equals(senderId)){
                Log.d("logging username" ,u.getUsername() );
                return u.getUsername();
            }
        }
        return "";
    }

    private String getUserProfilePic(String senderId) {
        for(User u : usersList){
            if(u.getUserId().equals(senderId)){
                Log.d("logging userprofile" ,u.getProfilePic() );
                return u.getProfilePic();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class messagesViewholder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, messageLocation, messageTime;
        ImageView userProfilePic;
        public messagesViewholder(@NonNull View itemView)
        {
            super(itemView);
            senderName = itemView.findViewById(R.id.tv_senderName_mg);
            messageText = itemView.findViewById(R.id.tv_message_mg);
            messageLocation = itemView.findViewById(R.id.tv_location_mg);
            messageTime = itemView.findViewById(R.id.tv_messageTime_mg);
            userProfilePic = itemView.findViewById(R.id.iv_userProfilePic_mg);

        }
    }


    @NonNull
    @Override
    public messagesViewholder onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.message_item, parent, false);
        return new messagesViewholder(view);
    }



    public interface MessagesInterface{
        void onItemClicked(Message message);
    }
}


