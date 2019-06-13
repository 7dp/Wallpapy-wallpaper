package id.radityo.wallpapy.MyFragment.Collections;

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
import id.radityo.wallpapy.MyFragment.Collections.Model.Collections;
import id.radityo.wallpapy.R;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsViewHolder> {
    private List<Collections> collectionsList;
    private Activity activity;

    public CollectionsAdapter(List<Collections> collectionsList, Activity activity) {
        this.collectionsList = collectionsList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_collections, viewGroup, false);
        return new CollectionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsViewHolder holder, int i) {
        final Collections item = collectionsList.get(i);

        Glide.with(holder.itemView.getContext())
                .load(item.getCoverPhoto().getUrls().getRegular())
                .error(R.drawable.ic_menu_gallery)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.imageView);

        holder.tvTitle.setText(item.getTitle());
        if (item.getTotalPhotos() < 2)
            holder.tvCount.setText(
                    String.valueOf(item.getTotalPhotos())
                            .concat(" ")
                            .concat(activity.getResources().getString(R.string.single)));
        else
            holder.tvCount.setText(
                    String.valueOf(item.getTotalPhotos())
                            .concat(" ")
                            .concat(activity.getResources().getString(R.string.non_single)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, DetailCollectionActivity.class);

                intent.putExtra("collection_id", item.getId());
                intent.putExtra("author_medium", item.getAuthor().getAuthorProfile().getMedium());
                intent.putExtra("author_name", item.getAuthor().getName());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("desc", item.getDescription());
                intent.putExtra("user_id", item.getAuthor().getId());
                intent.putExtra("user_name", item.getAuthor().getUsername());
                intent.putExtra("profile_image_small", item.getAuthor().getAuthorProfile().getSmall());
                intent.putExtra("profile_image_large", item.getAuthor().getAuthorProfile().getLarge());
                intent.putExtra("location", item.getAuthor().getLocation());
                intent.putExtra("bio", item.getAuthor().getBio());

                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return collectionsList.size();
    }

    class CollectionsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle, tvCount;


        CollectionsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_item_collectons);
            tvTitle = itemView.findViewById(R.id.tv_title_item_collections);
            tvCount = itemView.findViewById(R.id.tv_count_item_collections);
        }
    }
}
