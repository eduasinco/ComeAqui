package com.example.eduardorodriguez.comeaqui.order;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;

public class OrderFragment extends Fragment {

    FragmentManager fragmentManager;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        fragmentManager = getFragmentManager();
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    static class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment[] tabFragment = {PendingOrdersFragment.newInstance(true), PendingOrdersFragment.newInstance(false)};
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"pending orders", "past orders"};
            return titles[position];
        }
    }
}
