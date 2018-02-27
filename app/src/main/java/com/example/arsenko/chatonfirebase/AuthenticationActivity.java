package com.example.arsenko.chatonfirebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE-ME";
    private static final int RC_SIGN_IN = 100;

    @BindView(R.id.email_provider)
    CheckBox mUseEmailProvider;

    @BindView(R.id.phone_provider)
    CheckBox mUsePhoneProvider;

    @BindView(R.id.sign_in)
    Button mSignIn;

    @BindView(R.id.root)
    View mRootView;

    @BindView(R.id.credential_selector_enabled)
    CheckBox mEnableCredentialSelector;

    @BindView(R.id.hint_selector_enabled)
    CheckBox mEnableHintSelector;

    @BindView(R.id.allow_new_email_accounts)
    CheckBox mAllowNewEmailAccounts;


    public static Intent createIntent(Context context) {
        return new Intent(context, AuthenticationActivity.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_in)
    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(getSelectedProviders())
                        .setIsSmartLockEnabled(mEnableCredentialSelector.isChecked(),
                                mEnableHintSelector.isChecked())
                        .setAllowNewEmailAccounts(mAllowNewEmailAccounts.isChecked())
                        .build(),
                RC_SIGN_IN);
    }
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                handleSignInResponse(resultCode, data);
                return;
            }

            showSnackbar(R.string.unknown_response);
        }

        @Override
        protected void onResume() {
            super.onResume();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                startSignedInActivity(null);
                finish();
            }
        }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startSignedInActivity(response);
            finish();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }
        private void startSignedInActivity(IdpResponse response) {
            startActivity(SignedInActivity.createIntent(this, response,
                            new SignedInActivity.SignedInConfig(
                                    getSelectedProviders(),
                                    mEnableCredentialSelector.isChecked(),
                                    mEnableHintSelector.isChecked())));
        }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        if (mUseEmailProvider.isChecked()) {
            selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
        }

        if (mUsePhoneProvider.isChecked()) {
            selectedProviders.add(
                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build());
        }

        return selectedProviders;
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}