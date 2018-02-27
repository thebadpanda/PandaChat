package com.example.arsenko.chatonfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dialogs extends AppCompatActivity {
    @BindView(R.id.activities)
    RecyclerView mDialogs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        ButterKnife.bind(this);

        mDialogs.setLayoutManager(new LinearLayoutManager(this));
        mDialogs.setAdapter(new ActivityChooserAdapter());
        mDialogs.setHasFixedSize(true);
    }

    private static class ActivityChooserAdapter extends RecyclerView.Adapter<ActivityStarterHolder> {
        private static final Class[] CLASSES = new Class[]{
                AuthenticationActivity.class,
                MainActivity.class,
        };

        private static final int[] DESCRIPTION_NAMES = new int[]{
                R.string.title_auth_activity,
                R.string.title_realtime_database_activity,
        };

        private static final int[] DESCRIPTION_IDS = new int[]{
                R.string.desc_auth,
                R.string.desc_realtime_database,
        };

        @Override
        public ActivityStarterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ActivityStarterHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.dialog_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ActivityStarterHolder holder, int position){
            holder.bind(CLASSES[position], DESCRIPTION_NAMES[position], DESCRIPTION_IDS[position]);
        }

        @Override
        public int getItemCount() {
            return CLASSES.length;
        }
    }

    private static class ActivityStarterHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitle;
        private TextView mDescription;

        private Class mStarterClass;

        ActivityStarterHolder(View itemView){
            super(itemView);
            mTitle = itemView.findViewById(R.id.text1);
            mDescription = itemView.findViewById(R.id.text2);
        }

        private void bind(Class mClass, @StringRes int name, @StringRes int description) {
            mStarterClass = mClass;

            mTitle.setText(name);
            mDescription.setText(description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemView.getContext().startActivity(new Intent(itemView.getContext(), mStarterClass));
        }
    }
}



