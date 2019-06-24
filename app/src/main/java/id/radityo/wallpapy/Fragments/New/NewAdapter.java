package id.radityo.wallpapy.Fragments.New;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

import id.radityo.wallpapy.Activities.DetailActivity;
import id.radityo.wallpapy.Fragments.New.Model.New;
import id.radityo.wallpapy.R;

public class NewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<New> mNewList;

    public NewAdapter(Activity activity, List<New> newList) {
        this.mActivity = activity;
        this.mNewList = newList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == 1) {
            View view = LayoutInflater.from(
                    viewGroup.getContext())
                    .inflate(R.layout.loading_layout, viewGroup, false);
            return new LoadingViewHolder(view);

        } else {
            View view = LayoutInflater.from(
                    viewGroup.getContext())
                    .inflate(R.layout.item_latest, viewGroup, false);
            return new NewViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof NewViewHolder) {

            NewViewHolder nvh = (NewViewHolder) holder;
            final New aNew = mNewList.get(i);

            Glide.with(mActivity)
                    .load(aNew.getUrls().getRegular())
                    .error(R.drawable.ic_menu_gallery)
                    .placeholder(new ColorDrawable(Color.WHITE))
                    .fallback(new ColorDrawable(Color.GRAY))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(nvh.mImageView);

            if (aNew.getAuthor().getName() == null || aNew.getAuthor().getName().equals("null"))
                nvh.mTextView.setText(mActivity.getString(R.string.unknown));
            else nvh.mTextView.setText(aNew.getAuthor().getName());

            nvh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mActivity, DetailActivity.class);
                    intent.putExtra("url_regular", aNew.getUrls().getRegular());
                    intent.putExtra("id", aNew.getId());
                    intent.putExtra("color", aNew.getColor());

                    mActivity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mActivity,
                            Pair.create((View) ((NewViewHolder) holder).mImageView, "imageTransition")).toBundle());
                }
            });

        } else {
            LoadingViewHolder lvh = (LoadingViewHolder) holder;
            lvh.mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mNewList == null ? 0 : mNewList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mNewList.get(position) == null ? 1 : 0;
    }

    class NewViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        NewViewHolder(@NonNull View view) {
            super(view);
            mImageView = view.findViewById(R.id.iv_item_latest);
            mTextView = view.findViewById(R.id.tv_user_latest);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progress_loading);
        }
    }
}
