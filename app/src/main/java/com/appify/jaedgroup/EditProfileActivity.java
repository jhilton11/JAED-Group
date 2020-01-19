package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {
    private EditText nameET;
    private EditText emailEt;
    private EditText phoneEt;
    private ImageView profileImage;
    private EditText addressEt;
    private Button uploadBtn;
    private Button updateBtn;

    private StorageReference storageRef;
    private Uri file = null;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //TODO: Create id for EditProfileActivity layout in layout xml.
        nameET = findViewById(R.id.name_et);
        emailEt = findViewById(R.id.email_et);
        phoneEt = findViewById(R.id.phone_no_et);
        addressEt = findViewById(R.id.address_et);

        profileImage = findViewById(R.id.profile_Image);

        uploadBtn = findViewById(R.id.change_image_btn);
        updateBtn = findViewById(R.id.update_btn);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tasks.checkNetworkStatus(EditProfileActivity.this)) {
                    updateProfile();
                } else {
                    tasks.makeSnackbar(layout, "Cannot update provide because there is no network");
                }
            }
        });

    }

    private void changeImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, Constants.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE && resultCode == RESULT_OK) {
            file = data.getData();
            profileImage.setImageURI(file);
        }
    }

    private void updateProfile() {
        if (file != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");
            storageRef.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                    } else {
                        Log.d("msg", "Unable to upload because: " + task.getException());
                    }
                }
            });
        } else {

        }
    }
}
