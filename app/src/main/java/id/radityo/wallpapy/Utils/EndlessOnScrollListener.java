package id.radityo.wallpapy.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean mIsLoading = true;
    private int mPreviousTotal = 0;
    private int mCurrentPage = 1;

    public EndlessOnScrollListener() {
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (mIsLoading) {
            if (totalItemCount > mPreviousTotal) {
                mIsLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }

        int visibleThreshold = 2;

        if (!mIsLoading && (totalItemCount - visibleItemCount <= (firstVisibleItem + visibleThreshold))) {
            mIsLoading = true;
            mCurrentPage++;
            onLoadMore(mCurrentPage);
        }
    }

    public abstract void onLoadMore(int page);

    public void resetState() {
        this.mCurrentPage = 0;
        this.mPreviousTotal = 0;
        this.mIsLoading = true;
        mCurrentPage++;
    }
}
