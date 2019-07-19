package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.FirebaseUser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.firebase.database.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.MainActivity.firebaseUser;

public class ProfileFragment extends Fragment {

    public static String[] data;
    static public User user = MainActivity.user;
    static public View view;

    static ImageView profileImageView;
    static ImageView messageImage;
    static TextView emailView;
    static TextView bioView;
    static TextView nameView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static void setProfile(JsonObject jsonObject){
        user = new User(jsonObject);

        if(!user.profile_photo.contains("no-image")) Glide.with(view.getContext()).load(user.profile_photo).into(profileImageView);
        nameView.setText(user.first_name + " " + user.last_name);
        emailView.setText(user.email);
        bioView.setText(user.bio);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView backGroundImageView = view.findViewById(R.id.backGroundImage);

        profileImageView = view.findViewById(R.id.profile_image);
        messageImage = view.findViewById(R.id.message);
        emailView = view.findViewById(R.id.senderEmail);
        bioView = view.findViewById(R.id.bioView);
        nameView = view.findViewById(R.id.nameView);

        final CircularImageView circularImageView = view.findViewById(R.id.profile_image);
        circularImageView.setBorderColor(getResources().getColor(R.color.colorPrimary));
        circularImageView.setBorderWidth(10);

        final ImageView mImage =  view.findViewById(R.id.profile_image);
        AppBarLayout mAppBar = view.findViewById(R.id.app_bar);
        mAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            mImage.setY(-verticalOffset / 2 + 100);
            circularImageView.setShadowRadius(0 - verticalOffset / 5);
            backGroundImageView.setY(-verticalOffset / 4);
        });

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        String userEmail = getArguments() != null ? getArguments().getString("user_email") : null;
        if (userEmail != null) {
            messageImage.setVisibility(View.VISIBLE);

            GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/profile_detail/" + userEmail + "/");
            try {
                String response = process.execute().get();
                if (response != null)
                    setProfile(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            messageImage.setOnClickListener(v -> goToConversationActivity(userEmail));
        } else {
            mImage.setOnClickListener(v -> {
                Intent editProfile = new Intent(getContext(), EditProfileActivity.class);
                editProfile.putExtra("object", user);
                getContext().startActivity(editProfile);
            });

            GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_profile/");
            try {
                String response = process.execute().get();
                if (response != null)
                    setProfile(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    void goToConversationActivity(String email){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
        reference
            .orderByChild("signature")
            .equalTo("(" + MainActivity.user.email + ", " + email + ")")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ChatFirebaseObject chat = null;
                    if (dataSnapshot.getChildren().iterator().hasNext()){
                         chat = dataSnapshot.getChildren().iterator().next().getValue(ChatFirebaseObject.class);
                    }
                    if (chat == null){
                        reference
                            .orderByChild("signature")
                            .equalTo("(" + email + ", " + MainActivity.user.email  + ")")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ChatFirebaseObject chat = null;
                                        if (dataSnapshot.getChildren().iterator().hasNext()){
                                            chat = dataSnapshot.getChildren().iterator().next().getValue(ChatFirebaseObject.class);
                                        }
                                        if (chat !=  null) {
                                            goToConversationActivity(chat);
                                        } else {
                                            createNewChatAndGoToIt(email);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    } else {
                        goToConversationActivity(chat);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
    }
    void goToConversationActivity(ChatFirebaseObject chat){
        Intent k = new Intent(getContext(), ConversationActivity.class);
        k.putExtra("chat", chat);
        startActivity(k);
    }
    void createNewChatAndGoToIt(String email){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("email")
                .equalTo(email)
                .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ChatFirebaseObject chat = new ChatFirebaseObject();
                    chat.user1 = firebaseUser;
                    chat.user2 = dataSnapshot.getChildren().iterator().next().getValue(FirebaseUser.class);
                    chat.user2.id = dataSnapshot.getChildren().iterator().next().getKey();
                    chat.signature = "(" + chat.user1.email + ", " + chat.user2.email + ")";

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");
                    chat.id = reference.push().getKey();
                    reference.child(chat.id).setValue(chat);

                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("userChats");
                    reference2.child(chat.user1.id).push().setValue(chat.id);
                    reference2.child(chat.user2.id).push().setValue(chat.id);

                    goToConversationActivity(chat);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
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
            Fragment[] tabFragment = {new UserPostFragment(), new OptionsFragment(), new OptionsFragment()};
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"posts", "posts & reviews", "whatever"};
            return titles[position];
        }
    }

}