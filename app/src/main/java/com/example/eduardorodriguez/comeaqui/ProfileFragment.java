package com.example.eduardorodriguez.comeaqui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    public static ArrayList<String[]> data;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        final CircularImageView circularImageView = view.findViewById(R.id.profile_image);
        circularImageView.setBorderColor(getResources().getColor(R.color.colorPrimary));
        circularImageView.setBorderWidth(10);

        final ImageView mImage =  view.findViewById(R.id.profile_image);
        AppBarLayout mAppBar = view.findViewById(R.id.app_bar);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mImage.setY(-verticalOffset / 2 + 100);
                circularImageView.setShadowRadius(0 - verticalOffset / 5);
            }
        });

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scrolling, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new TestFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"posts", "messages", "likes"};
            return titles[position];
        }
    }

    public static class TestFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_page, container, false);
            RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new TestAdapter());
            return view;
        }
    }

    static class TestAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public int getItemCount() {
            return GetFoodFragment.data.size();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.getfood_list_element, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView foodNameView = holder.itemView.findViewById(R.id.foodName);
            foodNameView.setText(GetFoodFragment.data.get(position)[0]);
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}