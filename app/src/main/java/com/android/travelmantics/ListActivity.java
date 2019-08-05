package com.android.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.travelmantics.adapters.RvRowAdapter;
import com.android.travelmantics.utils.FirebaseUtil;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    private ArrayList<TravelDeal> travelDeals = new ArrayList<>();


    private RvRowAdapter rvRowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initViews();
        registerChildEvent();
    }

    private void initViews() {
        //Views
        RecyclerView rvDealsRecycler = findViewById(R.id.rvDeals);

        this.rvRowAdapter = new RvRowAdapter(this, travelDeals);
        rvDealsRecycler.setAdapter(rvRowAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvDealsRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvDealsRecycler.setLayoutManager(manager);
    }

    private void registerChildEvent() {
        this.mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
                if (deal != null) {
                    deal.setId(dataSnapshot.getKey());
                    travelDeals.add(deal);
                    rvRowAdapter.notifyItemInserted(travelDeals.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
                if (deal != null) {
                    travelDeals.remove(deal);
                    rvRowAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);

        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        } else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu: {
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.logout_menu: {
                this.logout();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUtil.attachListener();
                    }
                });
        FirebaseUtil.detachAuthListener();
    }

    private void fetchData() {
        FirebaseUtil.openFbReference("traveldeals", this);
        this.mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        this.mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mDatabaseReference.addChildEventListener(mChildListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
        FirebaseUtil.attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachAuthListener();
    }
}
