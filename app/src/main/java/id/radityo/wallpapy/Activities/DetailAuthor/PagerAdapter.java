package id.radityo.wallpapy.Activities.DetailAuthor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabPhotos();
            case 1:
                return new TabUserLikes();
            case 2:
                return new TabCollections();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
