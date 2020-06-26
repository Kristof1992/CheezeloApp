package com.example.chezelooapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.example.chezelooapp.adapters.CheeseAdapter;
import com.example.chezelooapp.adapters.WineAdapter;
import com.example.chezelooapp.models.IsAdministrator;
import com.example.chezelooapp.models.Product;
import com.example.chezelooapp.models.User;
import com.example.chezelooapp.ui.additem.AddItem;
import com.example.chezelooapp.ui.cheese.CheeseFragment;
import com.example.chezelooapp.ui.wine.WineFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements AddItem.OnAddItem, CheeseFragment.ILoadCheese, WineFragment.ILoadWine, IsAdministrator {
    private static final String TAG = "HomeActivity";
    //FIREBASE
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    public static Bitmap mBitMapImg;
    private NavigationView mNavigationView;
    private boolean isAdministrator = false;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_addItem, R.id.nav_cheese,
                R.id.nav_wine)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id == R.id.nav_logOut) {

                    FirebaseAuth.getInstance().signOut();

                    launchHome();
                    return true;

                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //UPDATE DRAWER UI
        uploadUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //ADD ITEM REALTIMEDATABASE
    @Override
    public void addItem(String title, String description, String type, String country, String pairs, boolean isCheese) {
        uploadImgToStorage(title, description, type, country, pairs, isCheese);
    }

    //CHECK PERMISSION - Hard coded integer as the requestPermissions accepts an integer as 2nd param
    private static final int PERMISSION_CODE = 1000;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //permission not enable, request it
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                //permission already granted
                loadImage();
            }
        } else {
            //system os < marshmallow
            loadImage();
        }
    }

    //ON REQUEST PERMISSON
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImage();
                } else {
                    Toast.makeText(this, "Cannot access your images", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private final int PICK_IMAGE_CODE = 2222;

    //LOAD IMAGE
    private void loadImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, PICK_IMAGE_CODE);
    }

    //ON ACTIVITY RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri imgUri = data.getData();
                mBitMapImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                ImageView mImage = findViewById(R.id.addItem_img);
                mImage.setImageBitmap(mBitMapImg);
            } catch (Exception exception) {
                Log.d(TAG, "onActivityResult: " + exception.toString());
            }
        }
    }

    //ADD IMAGE
    @Override
    public void addImageMethod(View view) {
        checkPermission();
    }

    //UPLOAD IMG TO FIREBASE STORAGE
    private void uploadImgToStorage(String title, String description, String type, String country, String pairs, boolean isCheese) {

        if (mBitMapImg != null) {
            byte[] myImage = convertToByte(mBitMapImg);
            //creating the image path using the first character of the user email address and dateformat
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault());
            //TODO : changeable
            Date dateObj = new Date();
            String imagePath = dateFormat.format(dateObj) + ".jpg";
            StorageReference mReference = mStorageRef.child("images/" + imagePath);
            Log.d(TAG, "uploadImgToStorage: before");
            mReference.putBytes(myImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task downloadUrl = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            final String urlValue = o.toString();
                            Log.d(TAG, "onSuccess: This Url" + urlValue);
                            // Uploads a cheese object
                            if (isCheese) {
                                myRef.child("Cheese").push().setValue(new Product(title, description, type, country, pairs, urlValue.toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Uploaded Succesfully!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                // Uploads a wine object
                            } else {
                                myRef.child("Wine").push().setValue(new Product(title, description, type, country, pairs, urlValue.toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Uploaded Succesfully!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }
                    });
                }
            });
        }
    }

    //CONVERT BITMAP TO BYTE
    public byte[] convertToByte(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();

    }

    //LOAD CHEESE FROM FIREBASE
    @Override
    public void loadWine(List<Product> mList, WineAdapter mAdapter) {
        // Read from the database
        myRef.child("Wine").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product wine = postSnapshot.getValue(Product.class);
                    wine.setKey(postSnapshot.getKey());
                    addInOrder(mList, wine);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //LOAD WINE FROM FIREBASE
    @Override
    public void loadCheese(List<Product> mList, CheeseAdapter mAdapter) {
        // Read from the database
        myRef.child("Cheese").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "loadCheese: ");
                    Product cheese = postSnapshot.getValue(Product.class);
                    cheese.setKey(postSnapshot.getKey());
                    addInOrder(mList, cheese);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void launchHome() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void uploadUserDetails() {
        Log.d(TAG, "uploadUserDetails: started");
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            final String UID = currentFirebaseUser.getUid();
            Log.d(TAG, "uploadUserDetails: UID " + UID);
            if (UID != null) {
                DatabaseReference ref = database.getReference("Users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "loadCheese: ");
                            User user = postSnapshot.getValue(User.class);
                            Log.d(TAG, "onDataChange: " + user.toString());
                            if (user.getUID().equals(UID)) {
                                updateUI(user);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "onComplete: Exception UID " + e.getMessage());
        }
    }

    // Also check for Admin here
    private void updateUI(User user) {
        View hView = mNavigationView.getHeaderView(0);
        if (user != null && hView != null) {
            ImageView userImg = hView.findViewById(R.id.drawerImg);
            TextView txtName = hView.findViewById(R.id.drawerName);
            TextView txtEmail = hView.findViewById(R.id.drawerEmail);
            // Picasso API used here to process the image and load it to the user profile pic
            Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.ic_person_holder_24dp).into(userImg);
            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
            //CHECK FOR ADMINISTRATOR
            checkAdmin(user.getEmail());
        }
    }

    // Hardcoded email for admin
    String ADMIN_USER = "admin@gmail.com";

    //admin password = admin1234
    private void checkAdmin(String admin) {
        if (admin.equals(ADMIN_USER)) {
            isAdministrator = true;
            Menu menuNav = mNavigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.nav_addItem);
            logoutItem.setVisible(true);
        }
    }

    @Override
    public boolean isAdministrator() {
        return isAdministrator;
    }

    @Override
    public void onLongClickPressed(String key, boolean isCheese) {
        if (isCheese) {
            myRef.child("Cheese").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Succesfully deleted", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail deleted" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            myRef.child("Wine").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Succesfully deleted", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail deleted" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    //ADD IN ORDER
    public static void addInOrder(List<Product> mList, Product product) {
        ListIterator<Product> productIterator = mList.listIterator();
        while (productIterator.hasNext()) {
            int comparison = productIterator.next().compareTo(product);

            if (comparison == 0) {
                Log.d(TAG, "addInOrder: Equal");
                return;
            } else if (comparison > 0) {
                //your new product is higher goes before
                productIterator.previous();
                productIterator.add(product);
                return;
            } else {
                //go further
            }
        }
        //your product is the smallest goes to the end of the list
        mList.add(product);
    }

}