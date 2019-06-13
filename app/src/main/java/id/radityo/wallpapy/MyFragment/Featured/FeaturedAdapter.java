package id.radityo.wallpapy.MyFragment.Featured;

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
import id.radityo.wallpapy.MyFragment.Featured.Model.Featured;
import id.radityo.wallpapy.R;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {
    private List<Featured> randomList;
    private Activity activity;

    FeaturedAdapter(List<Featured> randomList, Activity activity) {
        this.randomList = randomList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_latest, viewGroup, false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder vh, int i) {
        final Featured random = randomList.get(i);

        Glide.with(vh.itemView.getContext())
                .load(random.getUrls().getRegular())
                .error(R.drawable.ic_menu_gallery)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(vh.imageView);

        if (random.getAuthor().getName() == null || random.getAuthor().getName().equals("null"))
            vh.textView.setText(activity.getString(R.string.unknown));
        else vh.textView.setText(random.getAuthor().getName());

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtra("url_regular", random.getUrls().getRegular());
                intent.putExtra("id", random.getId());
                intent.putExtra("color", random.getColor());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return randomList.size();
    }

    class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        FeaturedViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_item_latest);
            textView = view.findViewById(R.id.tv_user_latest);
        }
    }
}
