package com.android.travelmantics;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.travelmantics.utils.FirebaseUtil;
import com.android.travelmantics.utils.ImageUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    public static int PICTURE_RESULT = 42;

    //Views
    private EditText xTxtTitle;
    private EditText xTxtPrice;
    private EditText xTxtDescription;
    private Button btnImage;
    private ImageView uploadImage;

    private TravelDeal travelDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        FirebaseUtil.openFbReference("traveldeals", this);
        this.mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        this.mDatabaseReference = FirebaseUtil.mDatabaseReference;

        initViews();
    }

    private void initViews() {
        xTxtTitle = findViewById(R.id.txtTitle);
        xTxtPrice = findViewById(R.id.txtPrice);
        xTxtDescription = findViewById(R.id.txtDescription);
        btnImage = findViewById(R.id.btnImage);
        uploadImage = findViewById(R.id.image);

        this.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
            }
        });

        this.travelDeal = getIntent().getParcelableExtra("deal");
        if (this.travelDeal == null) {
            this.travelDeal = new TravelDeal();
        }
        this.displayTravelDeal();
    }

    private void displayTravelDeal() {
        xTxtTitle.setText(this.travelDeal.getTitle());
        xTxtDescription.setText(this.travelDeal.getDescription());
        xTxtPrice.setText(this.travelDeal.getPrice());
        ImageUtils.loadImage(travelDeal.getImageUrl(), uploadImage, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            if (data == null) return;
            Uri uri = data.getData();
            final StorageReference imageRef = FirebaseUtil.storageReference.child(uri.getLastPathSegment());

            imageRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        if (downloadUrl != null) {
                            travelDeal.setImageUrl(downloadUrl.toString());
                            travelDeal.setImageName(task.getResult().getPath());
                            ImageUtils.loadImage(travelDeal.getImageUrl(), uploadImage,0,0);
                        }
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        MenuItem editMenu = menu.findItem(R.id.save_menu);
        MenuItem deleteMenu = menu.findItem(R.id.delete_menu);
        if (FirebaseUtil.isAdmin) {
            editMenu.setVisible(true);
            deleteMenu.setVisible(true);
            enableEditText(true);
        } else {
            editMenu.setVisible(false);
            deleteMenu.setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu: {
                saveDeal();
                Toast.makeText(this, "Deals Saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            }
            case R.id.delete_menu: {
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDeal() {
        travelDeal.setTitle(xTxtTitle.getText().toString());
        travelDeal.setPrice(xTxtPrice.getText().toString());
        travelDeal.setDescription(xTxtDescription.getText().toString());
        if (travelDeal.getId() == null) {
            this.mDatabaseReference.push().setValue(travelDeal);
        } else {
            this.mDatabaseReference.child(travelDeal.getId()).setValue(travelDeal);
        }
    }

    private void deleteDeal() {
        if (travelDeal == null) {
            Toast.makeText(this, "Please save deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        this.mDatabaseReference.child(travelDeal.getId()).removeValue();

        if(travelDeal.getImageName()!=null && !travelDeal.getImageName().isEmpty()){
//            StorageReference picRef = FirebaseUtil.mFirebaseDatabase.getReference().child(travelDeal.getImageName());
        }
    }

    private void enableEditText(boolean isEnabled) {
        xTxtTitle.setEnabled(isEnabled);
        xTxtDescription.setEnabled(isEnabled);
        xTxtPrice.setEnabled(isEnabled);
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {
        xTxtTitle.setText("");
        xTxtPrice.setText("");
        xTxtDescription.setText("");
    }

}
