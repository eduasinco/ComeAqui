package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ProfileFragment extends Fragment {

    public static String[] data;
    public View view;

    String email;
    String firstName;
    String lastName;
    String bio;
    String profile_photo;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void setProfile(ProfileFragment view, String jsonString){
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        JsonObject jo = jsonArray.get(0).getAsJsonObject();
        email = jo.get("email").getAsString();
        firstName = jo.get("first_name").getAsString();
        lastName = jo.get("last_name").getAsString();
        bio = jo.get("bio").getAsString();
        profile_photo = jo.get("profile_photo").getAsString();

        ImageView profileImageView = view.view.findViewById(R.id.profile_image);
        TextView emailView = view.view.findViewById(R.id.senderEmail);
        TextView bioView = view.view.findViewById(R.id.bioView);
        TextView nameView = view.view.findViewById(R.id.nameView);

        if(!profile_photo.contains("no-image")) Glide.with(view.view.getContext()).load(profile_photo).into(profileImageView);
        nameView.setText(firstName + " " + lastName);
        emailView.setText(email);
        bioView.setText(bio);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView backGroundImageView = view.findViewById(R.id.backGroundImage);

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
                backGroundImageView.setY(-verticalOffset / 4);
            }
        });


        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        mImage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Intent editProfile = new Intent(getContext(), EditProfileActivity.class);
                editProfile.putExtra("firstName", firstName);
                editProfile.putExtra("lastName", lastName);
                editProfile.putExtra("email", email);
                editProfile.putExtra("bio", bio);
                editProfile.putExtra("profile_photo", profile_photo);
                getContext().startActivity(editProfile);
            }
        });
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
            Fragment[] tabFragment = {new UserPostFragment(), new MessagesFragment(), new OptionsFragment()};
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"posts", "messages", "options"};
            return titles[position];
        }
    }

}