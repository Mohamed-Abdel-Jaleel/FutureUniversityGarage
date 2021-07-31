package com.fuegarage.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuegarage.MainActivity;
import com.fuegarage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;
import static com.fuegarage.MainActivity.myRef;

public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    Uri pickedImgUri = null ;


    //
    ImageView currentUserImageView;
    ImageButton profileImgageEditImageButton , profileNameEditButton ,profileNumberEditButton;
    ProgressBar imgProgressBar , saveProgressBar;
    EditText profileNameEditText ,profileNumberEditText;
    Button saveBtn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ///for Image
        currentUserImageView = view.findViewById(R.id.profile_user_img);
        imgProgressBar = view.findViewById(R.id.user_profile_img_progress_bar);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(currentUserImageView);
        profileImgageEditImageButton = view.findViewById(R.id.profile_user_photo_edit);
        profileImgageEditImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgProgressBar.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= 22) {
//                    openGallery();
                    checkAndRequestForPermission();
                }
                else {
                    openGallery();
                }
            }
        });
        ///for name
        String name = currentUser.getDisplayName().toString();
        profileNameEditText = view.findViewById(R.id.current_profile_user_name);
        profileNameEditButton=view.findViewById(R.id.current_profile_user_name_edit);
        profileNameEditText.setText(name);
        profileNameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileNameEditText.setEnabled(true);
            }
        });
        ///for phone number
        profileNumberEditText = view.findViewById(R.id.profile_phone_number);
        profileNumberEditButton = view.findViewById(R.id.profile_phone_number_edit);
        myRef.child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phoneNumber=snapshot.getValue(String.class);
                if(phoneNumber != null){
                    profileNumberEditText.setText(phoneNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profileNumberEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileNumberEditText.setEnabled(true);
            }
        });


        ///for save
        saveProgressBar = view.findViewById(R.id.user_profile_save_progress_bar);
        saveBtn = view.findViewById(R.id.profile_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProgressBar.setVisibility(View.VISIBLE);

                String name = profileNameEditText.getText().toString();
                String number = profileNumberEditText.getText().toString();
                UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                currentUser.updateProfile(profleUpdate)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myRef.child("name").setValue(name);
                                    myRef.child("phone").setValue(number);
                                    saveProgressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(getContext() , MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });


    }

    ///for Image
    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(),"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            openGallery();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {
            pickedImgUri = data.getData() ;
            currentUserImageView.setImageURI(pickedImgUri);
            /////////////////
            // first we need to upload user photo to firebase storage and get url
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // image uploaded succesfully
                    // now we can get our image url
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();
                            currentUser.updateProfile(profleUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                imgProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    });
                }
            });

        }
    }
    ///
}