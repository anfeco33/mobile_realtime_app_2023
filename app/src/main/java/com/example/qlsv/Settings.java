package com.example.qlsv;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.qlsv.databinding.FragmentSettingsBinding;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import io.github.muddz.styleabletoast.StyleableToast;

public class Settings extends Fragment {
    FragmentSettingsBinding binding;
    Uri ImageUri;
    Helper helper;
    private FirebaseAuth auth;
    Button btnLogout;
    ImageButton imgbtnEditImage;
    TextView tvRole, tvEmail, tvFullname, tvAge, tvPhone;
    ImageView profile_image;
    private static final int CUSTOM_REQUEST_CODE = 123;
    String cameraPermission[];
    String storagePermission[];
    private FirebaseDatabase database;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    private FirebaseStorage storage;
    private StorageReference storageReference = FirebaseStorage.getInstance("gs://qlsv-mobile.appspot.com").getReference();
    ProgressDialog dialog;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Activity mActivity;
    private String userPhoneNumber, userRole, userEmail, userFullname, userAge;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Settings() {
    }

    public static Settings newInstance(String phone, String role, String email, String fullname, String age) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString("phone", phone);
        args.putString("role", role);
        args.putString("email", email);
        args.putString("fullname", fullname);
        args.putString("age", age);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SettingsFragment", "onCreate called");
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Uploading....");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        helper = new Helper();

        if (getArguments() == null) {
            // redirect to login screen
            Intent intent = new Intent(getActivity(), LogIn.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            userPhoneNumber = getArguments().getString("phone");
            userRole = getArguments().getString("role");
            userEmail = getArguments().getString("email");
            userFullname = getArguments().getString("fullname");
            userAge = getArguments().getString("age");
            loadImage();

            activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Log.d("SettingsFragment", "ActivityResultLauncher - resultCode: " + result.getResultCode());
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            ImageUri = result.getData().getData();
                            binding.profileImage.setImageURI(ImageUri);
                            // Upload image to Firebase.
                            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(ImageUri));

                            fileRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("SettingsFragment", "Image upload successful");
                                            // get URL image and save it in db
                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    DatabaseReference userRef = databaseReference.child(userPhoneNumber);

                                                    userRef.child("image").setValue(uri.toString())
                                                            .addOnSuccessListener(aVoid -> {
                                                                StyleableToast.makeText(mActivity, "Your Profile Picture Have Been Successfully Updated", R.style.toast_custom).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                StyleableToast.makeText(mActivity, "Error updating profile picture", R.style.toast_custom).show();
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("SettingsFragment", "Image upload failed", e);
                                            StyleableToast.makeText(requireActivity(), "Sorry, there is error while uploading. Please try again later!", R.style.toast_custom).show();
                                        }
                                    });
                        } else {
                            Log.d("SettingsFragment", "Image selection failed or was canceled");
                            StyleableToast.makeText(requireActivity(), "Image selection cancelled or invalid image selected", R.style.toast_custom).show();
                        }
                    }
            );
        }
    }

    private void loadImage() {
        databaseReference.child(userPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        Picasso.get().load(R.drawable.avatar).into(binding.profileImage);
                    } else {
                        Picasso.get().invalidate(imageUrl);
                        Picasso.get().load(imageUrl).into(binding.profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SettingsFragment", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private String getFileExtension(Uri uri) {
        String extension = null;

        try {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        } catch (Exception e) {
            Log.e("SettingsFragment", "Error getting file extension", e);
        }

        if (extension == null) {
            // This handles cases where the file extension is not in the Uri
            String path = uri.getPath();
            if (path != null) {
                int lastDot = path.lastIndexOf('.');
                if (lastDot != -1) {
                    extension = path.substring(lastDot + 1);
                }
            }
        }
        Log.d("SettingsFragment", "File extension: " + extension);
        return extension;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_settings, container, false);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        btnLogout = view.findViewById(R.id.btnLogout);
        imgbtnEditImage = view.findViewById(R.id.imgbtnEditImage);
        profile_image = view.findViewById(R.id.profile_image);
        tvRole = view.findViewById(R.id.tvRole);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvFullname = view.findViewById(R.id.tvFullname);
        tvAge = view.findViewById(R.id.tvAge);
        tvPhone = view.findViewById(R.id.tvPhone);

        // show up information
        tvRole.setText(userRole.substring(0, 1).toUpperCase() + userRole.substring(1).toLowerCase());
        tvEmail.setText(userEmail);
        tvFullname.setText(userFullname);
        tvAge.setText(userAge);
        tvPhone.setText(userPhoneNumber);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                auth.signOut();

                Intent intent = new Intent(getActivity(), LogIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // select image from camera and gallery
        binding.imgbtnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        return view;
    }

    private void pickImage() {
        ImagePicker.Companion.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .saveDir(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                .createIntent(intent -> {
                    activityResultLauncher.launch(intent);
                    return null;
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CUSTOM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            binding.profileImage.setImageURI(selectedImageUri);
        }
    }
}