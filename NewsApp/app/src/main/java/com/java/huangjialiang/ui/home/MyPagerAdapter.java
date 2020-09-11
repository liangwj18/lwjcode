package com.java.huangjialiang.ui.home;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.function.Predicate;

public class MyPagerAdapter extends FragmentPagerItemAdapter {
    private FragmentPagerItems mPages;
    private int baseId;

    public MyPagerAdapter(FragmentManager fm, FragmentPagerItems pages) {
        super(fm, pages);
        mPages = pages;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    public void addItems(FragmentPagerItem item) {
        mPages.add(item);
    }

    public void removeAllViews(ViewPager pager) {
        pager.setAdapter(null);
        mPages.removeIf(new Predicate<FragmentPagerItem>() {
            @Override
            public boolean test(FragmentPagerItem fragmentPagerItem) {
                return fragmentPagerItem.getTitle() != "All";
            }
        });
        pager.setAdapter(this);
    }

    // 使fragment变化的关键
    @Override
    public long getItemId(int position) {
        return position == 0 ? 0 : baseId + position;
    }

    public void notifyChangeInPosition(int n) {
        baseId += getCount() + n;
    }
}
