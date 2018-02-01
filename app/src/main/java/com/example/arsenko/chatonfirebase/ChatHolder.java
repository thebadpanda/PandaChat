package com.example.arsenko.chatonfirebase;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatHolder extends RecyclerView.ViewHolder{
    private final TextView mNameField;
    private final TextView mTextField;
    private final RelativeLayout mMessageContainer;
    private final LinearLayout mMessage;
    private final int colorNotSender;
    private final int colorSender;


    public ChatHolder(View itemView) {
        super(itemView);

        mNameField = itemView.findViewById(R.id.message_text);
        mTextField = itemView.findViewById(R.id.name_text);
        mMessageContainer = itemView.findViewById(R.id.message_container);
        mMessage = itemView.findViewById(R.id.message);
        colorNotSender = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_500);
        colorSender = ContextCompat.getColor(itemView.getContext(), R.color.material_green_500);

    }

    public void bind(AbstractChat abstractChat){
        setName(abstractChat.getName());
        setText(abstractChat.getMessage());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        setIsSender(currentUser != null && abstractChat.getUid().equals(currentUser.getUid()));
    }


    private void setName(String name) {
        mNameField.setText(name);
    }

    private void setText(String text) {
        mTextField.setText(text);
    }

    private void setIsSender(boolean isSender ){
        final int color;
        if(isSender){
            color = colorSender;
            mMessageContainer.setGravity(Gravity.END);
        }else{
            color = colorNotSender;
            mMessageContainer.setGravity(Gravity.END);
        }
    }

}
