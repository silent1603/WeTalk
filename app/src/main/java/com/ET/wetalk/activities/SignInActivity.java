package com.ET.wetalk.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ET.wetalk.MainActivity;
import com.ET.wetalk.R;
import com.ET.wetalk.Utils.UI.LoadingDialog;
import com.ET.wetalk.databinding.ActivitySignInBinding;
import com.ET.wetalk.models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private LoadingDialog loadingDialog;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
        setup();
    }
    private void init()
    {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loadingDialog = new LoadingDialog(SignInActivity.this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void setup()
    {
        if(auth.getCurrentUser() != null)
        {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    private void setListeners()
    {
        binding.textCreateNewAccount.setOnClickListener( v -> startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.setContent("Login ...");
                loadingDialog.startLoadingDialog();
                auth.signInWithEmailAndPassword(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingDialog.dismissDialog();
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.btnGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }

    int RC_SIGN_IN = 65;
    private void googleSignIn()
    {
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("WeChat","firebaseAuthWithGoogle: "+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                Log.d("WeChat","Success");
            }catch (ApiException e)
            {
                Log.w("WeChat","Google sign in failed",e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("WeChat","signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilec(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);
                            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this,"Sign in with Google",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.w("WeChat","signInWithCredetial:failure",task.getException());
                            Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                             Snackbar.make(binding.getRoot(),"Authentication Failed",Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

}