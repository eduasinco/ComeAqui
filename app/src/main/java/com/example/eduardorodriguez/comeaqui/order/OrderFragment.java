package com.example.eduardorodriguez.comeaqui.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.MapCardFragment;

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
            PastOderFragment pastOderFragment = new PastOderFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putBoolean("pending", false);
            pastOderFragment.setArguments(bundle1);

            PastOderFragment pendingOderFragment = new PastOderFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean("pending", true);
            pendingOderFragment.setArguments(bundle2);

            Fragment[] tabFragment = {pendingOderFragment, pastOderFragment};
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
