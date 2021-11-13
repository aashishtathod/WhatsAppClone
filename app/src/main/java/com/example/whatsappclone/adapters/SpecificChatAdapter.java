package com.example.whatsappclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

        // adapter to show full chat or chatdetail of clicked user
public class SpecificChatAdapter extends RecyclerView.Adapter {//<RecyclerView.ViewHolder> {
    ArrayList<Message> messageArrayList;
    Context context;

    int sender = 1;
    int receiver = 2;

    public SpecificChatAdapter(ArrayList<Message> messageArrayList, Context context) {
        this.messageArrayList = messageArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == sender) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    // to differentiate btwn sender and reciever msg
    @Override
    public int getItemViewType(int position) {
        if (messageArrayList.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return sender;
        } else {
            return receiver;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        SimpleDateFormat sfd = new SimpleDateFormat("  dd-MM-yyyy \n  hh:mm aa");
        Message currentMessage = messageArrayList.get(position);

        if (holder.getClass() == SenderViewHolder.class) {

                    // if photo is sent by sender
            if (currentMessage.getMessage().equals("photo")) {
                ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                Picasso.get().load(currentMessage.getImageUrl()).placeholder(R.drawable.ic_image_placeholder).into(((SenderViewHolder) holder).senderImage);
                ((SenderViewHolder) holder).senderTime.setText(sfd.format(new Date(currentMessage.getTimeStamp())));

            } else {        //if text msg is sent by sender
                ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setText(currentMessage.getMessage());
                ((SenderViewHolder) holder).senderTime.setText(sfd.format(new Date(currentMessage.getTimeStamp())));
            }

        } else {
                        // if photo is recieved by reciever
            if (currentMessage.getMessage().equals("photo")) {
                ((ReceiverViewHolder) holder).receiverImage.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                Picasso.get().load(currentMessage.getImageUrl()).placeholder(R.drawable.ic_image_placeholder).into(((ReceiverViewHolder) holder).receiverImage);
                ((ReceiverViewHolder) holder).receiverTime.setText(sfd.format(new Date(currentMessage.getTimeStamp())));
                         // if text is recieved by reciever
            } else {
                ((ReceiverViewHolder) holder).receiverImage.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverMsg.setText(currentMessage.getMessage());
                ((ReceiverViewHolder) holder).receiverTime.setText(sfd.format(new Date(currentMessage.getTimeStamp())));
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;
        ImageView receiverImage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiver_text);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            receiverImage = itemView.findViewById(R.id.receiver_image);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        ImageView senderImage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.sender_text);
            senderTime = itemView.findViewById(R.id.sender_time);
            senderImage = itemView.findViewById(R.id.sender_image);
        }
    }
}
