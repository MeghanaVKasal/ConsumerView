package com.maucassignments.meghana.getitdone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.maucassignments.meghana.getitdone.adapter.FooditemsAdapter;
import com.maucassignments.meghana.getitdone.model.Post;

//import java.util.ArrayList;
//import java.util.List;
import java.util.*;
import java.lang.*;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsumerActivity extends AppCompatActivity implements FooditemsAdapter.OnFoodSelectedListener {
    @BindView(R.id.recycler_food_items_list)
    RecyclerView mfoodItemsRecycler;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FooditemsAdapter mAdapter;
    private static final int LIMIT = 50;
    private static final String TAG = "ConsumerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);


        ButterKnife.bind(this);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

    }
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("posts")
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new FooditemsAdapter(mQuery, this){

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mfoodItemsRecycler.setVisibility(View.GONE);
                    // mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mfoodItemsRecycler.setVisibility(View.VISIBLE);
                    // mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mfoodItemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mfoodItemsRecycler.setAdapter(mAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();

//        // Start sign in if necessary
//        if (shouldStartSignIn()) {
//            startSignIn();
//            return;
//        }

//        // Apply filters
//        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onFoodSelected(DocumentSnapshot postobj) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        Post mobj = postobj.toObject(Post.class);
        Bundle bundle = new Bundle();
        bundle.putString("bundleAddress", mobj.getAddress());
        //Get producer name
/*        String[] prodArray = mobj.getProducer().toString().split("/");
        String mCollection = prodArray[0];
        String docId = prodArray[1];
        DocumentReference docRef = mFirestore.collection(mCollection).document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot clickeduser = task.getResult();
                    if (clickeduser.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + clickeduser.get());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/
        bundle.putString("bundlePictureURL",mobj.getPictureURL());
        bundle.putString("bundleLatitude", String.valueOf(mobj.getLocation().getLatitude()));
        bundle.putString("bundleLongitude", String.valueOf(mobj.getLocation().getLongitude()));
        List<String> foodList= ((Post) mobj).getTags();
        String foodString = String.join(" ",foodList);
        bundle.putString("bundleFoodItems",foodString );
        bundle.putString("bundleProducer", mobj.getProducer().toString());
        bundle.putString("bundleDescription", mobj.getDescription());
        intent.putExtras(bundle);
        //postobj.toObject(Post.class).address
        startActivity(intent);
    }
}
