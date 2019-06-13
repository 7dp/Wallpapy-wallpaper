package id.radityo.wallpapy.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean isLoading = true;
    private int previousTotal = 0;
    private int currentPage = 1;
    private LinearLayoutManager layoutManager;

    public EndlessOnScrollListener() {
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (isLoading) {
            if (totalItemCount > previousTotal) {
                isLoading = false;
                previousTotal = totalItemCount;
            }
        }

        int visibleThresold = 2;

        if (!isLoading && (totalItemCount - visibleItemCount <= (firstVisibleItem + visibleThresold))) {
            isLoading = true;
            currentPage++;
            onLoadMore(currentPage);
        }
    }

    public abstract void onLoadMore(int page);

    public void resetState() {
        this.currentPage = 0;
        this.previousTotal = 0;
        this.isLoading = true;
        currentPage++;
    }
}
