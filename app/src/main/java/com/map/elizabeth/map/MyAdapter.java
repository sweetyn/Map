package com.map.elizabeth.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hyangmi.jo on 7/10/2016.
 */
public class MyAdapter extends FragmentPagerAdapter {

    public MyAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle info = new Bundle();
        switch (0) {
            case 0:
                FragmentA fragmenta = new FragmentA();

        }


        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
