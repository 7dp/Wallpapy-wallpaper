package id.radityo.wallpapy.MyFragment.New;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

import id.radityo.wallpapy.Activities.DetailActivity;
import id.radityo.wallpapy.MyFragment.New.Model.New;
import id.radityo.wallpapy.R;

public class NewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<New> newList;

    public NewAdapter(Activity activity, List<New> newList) {
        this.activity = activity;
        this.newList = newList;
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
            final New aNew = newList.get(i);

            Glide.with(activity)
                    .load(aNew.getUrls().getRegular())
                    .error(R.drawable.ic_menu_gallery)
                    .fallback(new ColorDrawable(Color.GRAY))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(nvh.imageView);

            if (aNew.getAuthor().getName() == null || aNew.getAuthor().getName().equals("null"))
                nvh.textView.setText(activity.getString(R.string.unknown));
            else nvh.textView.setText(aNew.getAuthor().getName());

            nvh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(activity, DetailActivity.class);
                    intent.putExtra("url_regular", aNew.getUrls().getRegular());
                    intent.putExtra("id", aNew.getId());
                    intent.putExtra("color", aNew.getColor());
                    activity.startActivity(intent);
                }
            });

        } else {
            LoadingViewHolder lvh = (LoadingViewHolder) holder;
            lvh.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return newList.get(position) == null ? 1 : 0;
    }

    class NewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        NewViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_item_latest);
            textView = view.findViewById(R.id.tv_user_latest);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_loading);
        }
    }
}
