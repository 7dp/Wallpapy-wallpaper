package id.radityo.wallpapy.Activities.Search;

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

import id.radityo.wallpapy.Activities.DetailAuthor.DetailAuthorActivity;
import id.radityo.wallpapy.Activities.DetailAuthor.User.User;
import id.radityo.wallpapy.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Activity activity;

    UserAdapter(List<User> userList, Activity activity) {
        this.userList = userList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int i) {
        final User user = userList.get(i);

        Glide.with(activity)
                .load(user.getProfileImage().getMedium())
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .placeholder(new ColorDrawable(Color.WHITE))
                .fallback(new ColorDrawable(Color.GRAY))
                .into(holder.imageView);

        holder.tvName.setText(user.getName());
        holder.tvUsername.setText("@".concat(user.getUsername()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailAuthorActivity.class);

                intent.putExtra("user_id", user.getId());
                intent.putExtra("name", user.getName());
                intent.putExtra("user_name", user.getUsername());
                intent.putExtra("profile_image_medium", user.getProfileImage().getMedium());
                intent.putExtra("profile_image_large", user.getProfileImage().getLarge());
                intent.putExtra("location", user.getLocation());
                intent.putExtra("bio", user.getBio());

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvUsername, tvName;

        UserViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_item_user);
            tvUsername = view.findViewById(R.id.tv_username_item_user);
            tvName = view.findViewById(R.id.tv_name_item_user);
        }
    }
}
