package com.example.arsenko.chatonfirebase;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static class ImeHelper {

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        public interface DonePressedListener {
            void onDonePressed();
        }

        static void setImeOnDoneListener(EditText doneEditText,
                                         final DonePressedListener listener) {
            doneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            listener.onDonePressed();
                        }
                        // We need to return true even if we didn't handle the event to continue
                        // receiving future callbacks.
                        return true;
                    } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                        listener.onDonePressed();
                        return true;
                    }
                    return false;
                }
            });
        }
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
}
