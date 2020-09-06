package com.example.newsapp.ui.home;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.function.Predicate;

public class MyPagerAdapter extends FragmentPagerItemAdapter {
    FragmentPagerItems mPages;

    public MyPagerAdapter(FragmentManager fm, FragmentPagerItems pages) {
        super(fm, pages);
        mPages = pages;
    }

    @Override
    public int getItemPosition(Object object) {
        int index = mPages.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
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
                return fragmentPagerItem.getTitle() != "ALL";
            }
        });
        pager.setAdapter(this);
    }
}
