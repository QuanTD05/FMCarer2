//package fpoly.md19304.instagram;
//
//import android.app.ProgressDialog;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.webkit.MimeTypeMap;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.android.gms.tasks.Continuation;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.StorageTask;
//
//import java.util.HashMap;
//
//
//public class PostActivity extends AppCompatActivity {
//    Uri imageUri;
//    String myUrl = "";
//    StorageTask uploadTask;
//    StorageReference storageReference;
//    ImageView close, image_added;
//    TextView post;
//    EditText description;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_post);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        close = findViewById(R.id.close);
//        image_added = findViewById(R.id.image_added);
//        post = findViewById(R.id.post);
//        description = findViewById(R.id.description);
//        storageReference = FirebaseStorage.getInstance().getReference("posts");
//
//        close.setOnClickListener(v -> startActivity(new Intent(PostActivity.this, MainActivity3.class)));
//        post.setOnClickListener(v -> uploadImage());
//
//
//    }
//
//private String getFileExtension(Uri uri){
//    ContentResolver contentResolver = getContentResolver();
//    MimeTypeMap mime = MimeTypeMap.getSingleton();
//    return mime.getExtensionFromMimeType(contentResolver.getType(uri));
//}
//
//    private void uploadImage() {
//        ProgressDialog  progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Postting");
//        progressDialog.show();
//
//        if (imageUri != null) {
//            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "."+getFileExtension(imageUri));
//            uploadTask = fileReference.putFile(imageUri);
//
//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()){
//                        throw  task.getException();
//                    }
//                    return fileReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()){
//                        Uri downloadUri = task.getResult();
//                        myUrl = downloadUri.toString();
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//                        String postid  = reference.push().getKey();
//
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("postid", postid);
//                        hashMap.put("postimage", myUrl);
//                        hashMap.put("description", description.getText().toString());
//                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                        reference.child(postid).setValue(hashMap);
//
//                        progressDialog.dismiss();
//                        startActivity(new Intent(PostActivity.this,MainActivity3.class));
//                        finish();
//                    }else {
//                        Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }else {
//            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Implement this method if you need to save post details to a database
//    private void savePostToDatabase(String imageUrl, String description) {
//        // Your logic to save post details
//    }
//}






//package fpoly.md19304.instagram;
//
//import android.app.ProgressDialog;
//import android.content.ContentResolver;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.webkit.MimeTypeMap;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.android.gms.tasks.Continuation;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.StorageTask;
//
//import java.util.HashMap;
//
//import fpoly.md19304.instagram.Fragment.PostFragment;
//
//public class PostActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private static final int CAPTURE_IMAGE_REQUEST = 2;
//
//    Uri imageUri;
//    String myUrl = "";
//    StorageTask uploadTask;
//    StorageReference storageReference;
//    ImageView close, image_added;
//    TextView post;
//    EditText description;
//    LinearLayout buttonSelectImage, buttonTakePhoto;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Initialize views
//        close = findViewById(R.id.close);
//        image_added = findViewById(R.id.image_added);
//        post = findViewById(R.id.post);
//        description = findViewById(R.id.description);
//        buttonSelectImage = findViewById(R.id.buttonSelectImage);
//        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
//
//        storageReference = FirebaseStorage.getInstance().getReference("posts");
//
//        // Set onClickListeners
//        close.setOnClickListener(v -> startActivity(new Intent(PostActivity.this, MainActivity3.class)));
//        post.setOnClickListener(v -> uploadImage());
//
//        // Button to select an image from the gallery
//        buttonSelectImage.setOnClickListener(v -> openGallery());
//
//        // Button to capture a photo using the camera
//        buttonTakePhoto.setOnClickListener(v -> openCamera());
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    private void openCamera() {
//        // Create an Intent to open the camera
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // Check if the device can handle the camera action
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
//                imageUri = data.getData();
//                image_added.setImageURI(imageUri);
//            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
//                // Get the image as a Bitmap and then convert it to Uri
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    imageUri = data.getData(); // Use this line only if you need the URI returned
//                    image_added.setImageBitmap((Bitmap) extras.get("data"));
//                }
//            }
//        }
//    }
//
//    private String getFileExtension(Uri uri) {
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
//
//    private void uploadImage() {
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Posting...");
//        progressDialog.show();
//
//        if (imageUri != null) {
//            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
//            uploadTask = fileReference.putFile(imageUri);
//
//            uploadTask.continueWithTask(new Continuation<Uri, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<Uri> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return fileReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        myUrl = downloadUri.toString();
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//                        String postid = reference.push().getKey();
//
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("postid", postid);
//                        hashMap.put("postimage", myUrl);
//                        hashMap.put("description", description.getText().toString());
//                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                        reference.child(postid).setValue(hashMap);
//
//                        progressDialog.dismiss();
//                        startActivity(new Intent(PostActivity.this, MainActivity3.class));
//                        finish();
//                    } else {
//                        Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }).addOnFailureListener(e -> {
//                Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        } else {
//            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Implement this method if you need to save post details to a database
//    private void savePostToDatabase(String imageUrl, String description) {
//        // Your logic to save post details
//    }
//}

// === PostActivity.java ===
package com.example.fmcarer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private ImageView close, image_added;
    private TextView post;
    private EditText description;
    private Spinner spinnerShareLevel;
    private LinearLayout buttonSelectImage, buttonTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        spinnerShareLevel = findViewById(R.id.spinnerShareLevel);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(v -> startActivity(new Intent(PostActivity.this, MainActivity3.class)));

        post.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                Toast.makeText(PostActivity.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSelectImage.setOnClickListener(v -> openGallery());
        buttonTakePhoto.setOnClickListener(v -> openCamera());
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                image_added.setImageURI(imageUri);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUriFromBitmap(bitmap);
                image_added.setImageBitmap(bitmap);
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng bài...");
        progressDialog.show();

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = (Uri) task.getResult();
                    myUrl = downloadUri.toString();
                    savePostToDatabase(myUrl);
                    progressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this, MainActivity3.class));
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Lỗi khi tải ảnh!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(PostActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Chưa chọn ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePostToDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userRole = snapshot.child("role").getValue(String.class);
                if (userRole == null) userRole = "main"; // phòng trường hợp chưa có

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                String postid = reference.push().getKey();

                String selectedLevel = spinnerShareLevel.getSelectedItem().toString();
                String shareLevel = selectedLevel.equals("Gia đình") ? "family" : "community";
                String status = shareLevel.equals("community") ? "pending" : "approved";

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("postid", postid);
                hashMap.put("postimage", imageUrl);
                hashMap.put("description", description.getText().toString());
                hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("shareLevel", shareLevel);
                hashMap.put("status", status);
                hashMap.put("userRole", userRole); // Lưu role vào post

                reference.child(postid).setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostActivity.this, "Lỗi khi lưu dữ liệu.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostActivity.this, "Lỗi khi lấy role!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
