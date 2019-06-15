package id.radityo.wallpapy.Fragments.Collections;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

import id.radityo.wallpapy.Activities.DetailCollectionActivity;
import id.radityo.wallpapy.Fragments.Collections.Model.Collections;
import id.radityo.wallpapy.R;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsViewHolder> {
    private List<Collections> mCollectionList;
    private Activity mActivity;

    public CollectionsAdapter(Activity activity, List<Collections> collectionsList) {
        this.mCollectionList = collectionsList;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public CollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_collections, viewGroup, false);
        return new CollectionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsViewHolder holder, int i) {
        final Collections collection = mCollectionList.get(i);

        Glide.with(holder.itemView.getContext())
                .load(collection.getCoverPhoto().getUrls().getRegular())
                .error(R.drawable.ic_menu_gallery)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.mImageView);

        holder.mTvTitle.setText(collection.getTitle());

        if (collection.getTotalPhotos() < 2)
            holder.mTvCount.setText(
                    String.valueOf(collection.getTotalPhotos()).concat(" ").concat(mActivity.getResources().getString(R.string.single)));
        else holder.mTvCount.setText(
                String.valueOf(collection.getTotalPhotos()).concat(" ").concat(mActivity.getResources().getString(R.string.non_single)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, DetailCollectionActivity.class);

                intent.putExtra("collection_id", collection.getId());
                intent.putExtra("author_medium", collection.getAuthor().getAuthorProfile().getMedium());
                intent.putExtra("author_name", collection.getAuthor().getName());
                intent.putExtra("title", collection.getTitle());
                intent.putExtra("desc", collection.getDescription());
                intent.putExtra("user_id", collection.getAuthor().getId());
                intent.putExtra("user_name", collection.getAuthor().getUsername());
                intent.putExtra("profile_image_small", collection.getAuthor().getAuthorProfile().getSmall());
                intent.putExtra("profile_image_large", collection.getAuthor().getAuthorProfile().getLarge());
                intent.putExtra("location", collection.getAuthor().getLocation());
                intent.putExtra("bio", collection.getAuthor().getBio());

                mActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCollectionList.size();
    }

    class CollectionsViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTvTitle, mTvCount;

        CollectionsViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item_collectons);
            mTvTitle = itemView.findViewById(R.id.tv_title_item_collections);
            mTvCount = itemView.findViewById(R.id.tv_count_item_collections);
        }
    }
}
