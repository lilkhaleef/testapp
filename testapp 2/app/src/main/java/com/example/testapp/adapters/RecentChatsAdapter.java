package com.example.testapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapp.databinding.ItemContainerRecentChatsBinding;
import com.example.testapp.listeners.ChatListener;
import com.example.testapp.models.ChatMessage;
import com.example.testapp.models.User;

import java.util.List;

public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.ChatViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ChatListener chatListener;

    public RecentChatsAdapter(List<ChatMessage> chatMessages, ChatListener chatListener) {

        this.chatMessages = chatMessages;
        this.chatListener = chatListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(
                ItemContainerRecentChatsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
    holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentChatsBinding binding;

        ChatViewHolder(ItemContainerRecentChatsBinding itemContainerRecentChatsBinding){
            super(itemContainerRecentChatsBinding.getRoot());
            binding =itemContainerRecentChatsBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.imageProfile.setImageBitmap(getChatImage(chatMessage.chatImage));
            binding.textName.setText(chatMessage.chatName);
            binding.recentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id =chatMessage.chatId;
                user.image = chatMessage.chatImage;
                user.name = chatMessage.chatName;
                chatListener.onChatClicked(user);
            });
        }
    }

    private Bitmap getChatImage(String encodedImage){
        byte [] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
