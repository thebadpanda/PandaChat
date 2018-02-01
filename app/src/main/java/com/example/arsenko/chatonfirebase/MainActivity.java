package com.example.arsenko.chatonfirebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{

    private final static int SIGN_IN_REQUEST_CODE = 100;

    Query query = FirebaseDatabase.getInstance().getReference().child("chats").limitToLast(50);

    @BindView(R.id.sendButton)
    FloatingActionButton mSendButton;

    @BindView(R.id.inputText)
    EditText mInputText;

    @BindView(R.id.messagesList)
    RecyclerView mMessageList;

    @BindView(R.id.emptyTextView)
    TextView mEmptyListMessage;

    @SuppressLint("RestrictedApi")  // because imehelper had an error
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);

        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(new LinearLayoutManager(this));

        ImeHelper.setImeOnDoneListener(mInputText, new ImeHelper.DonePressedListener() {
            @Override
            public void onDonePressed() {
                onSendClick();
            }
        });
    }
//        // SIGN IN
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
//    } else {
//        Toast.makeText(MainActivity.this, "Welcome!" + FirebaseAuth.getInstance().getCurrentUser()
//                .getDisplayName(), Toast.LENGTH_SHORT).show();
//
//        displayChatMessages();
//    }
    @Override
    public void onStart(){
        super.onStart();
        if(isSignedIn()){
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mSendButton.setEnabled(isSignedIn());
        mInputText.setEnabled(isSignedIn());

        if(isSignedIn()){
            attachRecyclerViewAdapter();

        }else{
            Toast.makeText(this, "Signing in ", Toast.LENGTH_SHORT).show();
            firebaseAuth.signInAnonymously().addOnCompleteListener(new UserStateNotifier(this));
        }
    }

    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                mMessageList.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        mMessageList.setAdapter(adapter);
    }

    @OnClick(R.id.sendButton)
    public void onSendClick() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = "User" + uid.substring(0, 6);
//        long time = new Date().getTime();

        onAddMessage(new ChatMessage(name, mInputText.getText().toString(), uid));

        mInputText.setText("");
    }

    protected RecyclerView.Adapter newAdapter(){
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLifecycleOwner(this)
                .build();

        return new FirebaseRecyclerAdapter<ChatMessage, ChatHolder>(options) {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChatHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false));
            }
            @Override
            protected void  onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatMessage model) {
                holder.bind(model);
            }
            @Override
            public void onDataChanged(){
                mEmptyListMessage.setVisibility(getItemCount() == 0? View.VISIBLE : View.GONE);
            }
        };
    }

    protected void onAddMessage(ChatMessage chat) {
        query.getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                if (error != null) {
                    Log.e("Log: ", "Failed to write message", error.toException());
                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(MainActivity.this, "You are signed in. Welcome!", Toast.LENGTH_SHORT).show();

                displayChatMessages();
            }
            else{
                Toast.makeText(MainActivity.this, "Please sign in for chating !", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "You've been signed out", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        return true;
    }

    private void displayChatMessages() {
        RecyclerView listOfMessages = findViewById(R.id.messagesList);

        Query query = FirebaseDatabase.getInstance()
                .getReference().child("chats");

        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .build();

       FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatHolder>(options) {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false);

                return new ChatHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatMessage model) {

            }
        };
//        listOfMessages.setAdapter(adapter);
    }








}
