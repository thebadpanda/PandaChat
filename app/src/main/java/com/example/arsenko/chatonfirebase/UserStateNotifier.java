package com.example.arsenko.chatonfirebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class UserStateNotifier implements OnCompleteListener<AuthResult> {

    private Context mContext;

    UserStateNotifier(Context context){
        mContext = context.getApplicationContext();

    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
            Toast.makeText(mContext, "You are sign in !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "Authorization failed !!!", Toast.LENGTH_SHORT).show();
        }

    }
}
