package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.appify.jaedgroup.model.User;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class EditProfileActivity extends AppCompatActivity {
    private EditText nameEt;
    private EditText phoneEt;
    private ImageView profileImage;
    private Button uploadBtn;
    private Button updateBtn;
    private ProgressDialog dialog;

    private Uri file = null;
    private View layout;

    private String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //TODO: Create id for EditProfileActivity layout in layout xml.
        nameEt = findViewById(R.id.name_et);
        phoneEt = findViewById(R.id.phone_no_et);

        profileImage = findViewById(R.id.profile_Image);

        uploadBtn = findViewById(R.id.change_image_btn);
        updateBtn = findViewById(R.id.update_btn);

        dialog = new ProgressDialog(this);

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
                    dialog.show();
                    updateProfile();
                } else {
                    tasks.makeSnackbar(layout, "Cannot update provide because there is no network");
                }
            }
        });

        updateUI();

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
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
            storageRef.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    addtoDatabase(task.getResult().toString());
                                }
                            }
                        });
                    } else {
                        Log.d("msg", "Unable to upload because: " + task.getException());
                    }
                }
            });
        } else {
            addtoDatabase("");
        }
    }

    private void addtoDatabase(String imageUrl) {
        User user = new User(id, nameEt.getText().toString(), phoneEt.getText().toString());
        user.setImageUrl(imageUrl);
        user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("users");
        DocumentReference docRef = colRef.document(id);
        docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });
    }

    private void updateUI() {
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("users");
        DocumentReference docRef = colRef.document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                if (user.getName()!=null && user.getName().length()>0) {
                    nameEt.setText(user.getName());
                }

                if (!TextUtils.isEmpty(user.getPhoneNo())) {
                    phoneEt.setText(user.getPhoneNo());
                }

                if (TextUtils.isEmpty(user.getImageUrl())) {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profileImage);
                }
            }
        });
    }

}
