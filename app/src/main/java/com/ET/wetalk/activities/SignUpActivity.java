package com.ET.wetalk.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.ET.wetalk.Utils.UI.LoadingDialog;
import com.ET.wetalk.databinding.ActivitySignUpBinding;
import com.ET.wetalk.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String encodedImage;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();

    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        loadingDialog = new LoadingDialog(SignUpActivity.this);

    }

    private void setListeners() {

        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidSignUpDetails())
                {
                    signUp();
                }
            }
        });
        binding.layoutImage.setOnClickListener( view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 150;
        int previewHeight =bitmap.getHeight() * previewWidth /bitmap.getWidth();
        Bitmap prewviewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewWidth,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        prewviewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result ->
        {
            if(result.getResultCode() == RESULT_OK)
            {
                if(result.getData() != null)
                {
                    Uri imageUri = result.getData().getData();
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                    }catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    );

    private void signUp() {
        loadingDialog.setContent("Create Account ...");
        loadingDialog.startLoadingDialog();
        auth.createUserWithEmailAndPassword(binding.inputEmail.getText().toString(), binding
                .inputPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismissDialog();
                if (task.isSuccessful()) {
                    Users user = new Users(encodedImage,binding.inputName.getText().toString(), binding.inputEmail.getText().toString(), binding.inputPassword.getText().toString());
                    String id = task.getResult().getUser().getUid();
                    database.getReference().child("Users").child(id).setValue(user);
                    Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Boolean isValidSignUpDetails()
    {

        if (encodedImage == null) {
            showToast("Select profile image");
            return false;
        }

        if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter name");
            return false;
        }

        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        }

        if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }

        if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }

        if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Confirm password");
            return false;
        }

        if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString()))
        {
            showToast("Password & confirm password must be same");
            return  false;
        }
        return true;
    }
}

