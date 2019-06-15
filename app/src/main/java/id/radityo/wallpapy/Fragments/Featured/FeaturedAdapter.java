package id.radityo.wallpapy.Fragments.Featured;

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

import id.radityo.wallpapy.Activities.DetailActivity;
import id.radityo.wallpapy.Fragments.Featured.Model.Featured;
import id.radityo.wallpapy.R;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {
    private List<Featured> mRandomList;
    private Activity mActivity;

    FeaturedAdapter(Activity activity, List<Featured> randomList) {
        this.mRandomList = randomList;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_latest, viewGroup, false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder vh, int i) {
        final Featured featured = mRandomList.get(i);

        Glide.with(vh.itemView.getContext())
                .load(featured.getUrls().getRegular())
                .error(R.drawable.ic_menu_gallery)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(vh.mImageView);

        if (featured.getAuthor().getName() == null || featured.getAuthor().getName().equals("null"))
            vh.mTextView.setText(mActivity.getString(R.string.unknown));
        else vh.mTextView.setText(featured.getAuthor().getName());

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra("url_regular", featured.getUrls().getRegular());
                intent.putExtra("id", featured.getId());
                intent.putExtra("color", featured.getColor());
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRandomList.size();
    }

    class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        FeaturedViewHolder(@NonNull View view) {
            super(view);
            mImageView = view.findViewById(R.id.iv_item_latest);
            mTextView = view.findViewById(R.id.tv_user_latest);
        }
    }
}
