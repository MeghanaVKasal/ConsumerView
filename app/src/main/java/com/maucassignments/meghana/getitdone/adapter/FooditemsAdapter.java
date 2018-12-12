/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.maucassignments.meghana.getitdone.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.maucassignments.meghana.getitdone.R;
import com.maucassignments.meghana.getitdone.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class FooditemsAdapter extends FirestoreAdapter<FooditemsAdapter.ViewHolder> {

    public interface OnFoodSelectedListener {

        void onFoodSelected(DocumentSnapshot postobj);

    }

    private OnFoodSelectedListener mListener;

    public FooditemsAdapter(Query query, OnFoodSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.food_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.food_items)
        TextView foodItemsView;
        @BindView(R.id.food_address)
        TextView foodAddressView;
        @BindView(R.id.food_producerName)
        TextView foodProducerNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnFoodSelectedListener listener) {
            Post postobj = snapshot.toObject(Post.class);

            List<String> foodList= ((Post) postobj).getTags();
            String foodString;
            if(foodList != null) {
               foodString = String.join(" ", foodList);
            }else{
                foodString = " ";
            }
            foodItemsView.setText(foodString);
            foodAddressView.setText(postobj.getAddress());
            foodProducerNameView.setText(postobj.getProducer());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onFoodSelected(snapshot);
                    }
                }
            });
        }

    }
}
