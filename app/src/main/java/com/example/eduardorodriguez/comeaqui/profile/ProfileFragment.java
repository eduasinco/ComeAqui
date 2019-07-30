package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.eduardorodriguez.comeaqui.chat.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.FirebaseUser;
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
import com.google.gson.JsonParser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.MainActivity.firebaseUser;

public class ProfileFragment extends Fragment {

    public static String[] data;
    public View view;
    public User user;
    boolean isBackGound;

    private ImageView profileImageView;
    private ImageView backGroundImage;
    private TextView emailView;
    private TextView bioView;
    private TextView nameView;
    private ConstraintLayout outOfCard;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void setProfile(User user){
        if(!user.profile_photo.contains("no-image")) Glide.with(view.getContext()).load(user.profile_photo).into(profileImageView);
        if(!user.background_photo.contains("no-image")) Glide.with(view.getContext()).load(user.background_photo).into(backGroundImage);
        nameView.setText(user.first_name + " " + user.last_name);
        emailView.setText(user.email);
        bioView.setText(user.bio);
    }

    @Override
    public void onResume() {
        super.onResume();
        outOfCard.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView backGroundImageView = view.findViewById(R.id.backGroundImage);

        profileImageView = view.findViewById(R.id.profile_image);
        backGroundImage = view.findViewById(R.id.backGroundImage);
        ImageView messageImage = view.findViewById(R.id.message);
        emailView = view.findViewById(R.id.senderEmail);
        bioView = view.findViewById(R.id.bioView);
        nameView = view.findViewById(R.id.nameView);
        ImageView editProfileView = view.findViewById(R.id.edit_profile);
        ImageView addProfilePhotoView = view.findViewById(R.id.add_profile_photo);
        ImageView addBackGroundPhotoView = view.findViewById(R.id.add_background_photo);
        LinearLayout selectFromCamera = view.findViewById(R.id.select_from_camera);
        LinearLayout selectFromGallery = view.findViewById(R.id.select_from_gallery);
        outOfCard = view.findViewById(R.id.out_of_card);

        final CircularImageView circularImageView = view.findViewById(R.id.profile_image);
        final ImageView mImage =  view.findViewById(R.id.profile_image);
//        circularImageView.setBorderWidth(10);
//        AppBarLayout mAppBar = view.findViewById(R.id.app_bar);
//        mAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
//            mImage.setY(-verticalOffset / 2 + 100);
//            circularImageView.setShadowRadius(0 - verticalOffset / 5);
//            backGroundImageView.setY(-verticalOffset / 4);
//        });

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        user = getArguments() != null ? (User) getArguments().getSerializable("user_email") : null;
        if (user != null && user.id != MainActivity.user.id) {
            messageImage.setVisibility(View.VISIBLE);
            setProfile(user);
            messageImage.setOnClickListener(v -> goToConversationWithUser(user));
        } else {
            editProfileView.setVisibility(View.VISIBLE);
            editProfileView.setOnClickListener(v -> {
                Intent editProfile = new Intent(getContext(), EditProfileActivity.class);
                editProfile.putExtra("object", MainActivity.user);
                getContext().startActivity(editProfile);
            });
            setProfile(MainActivity.user);
        }

        int curveRadius = 40;

        view.findViewById(R.id.backGroundImage).setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight() + curveRadius), curveRadius);
            }
        });
        view.findViewById(R.id.backGroundImage).setClipToOutline(true);

        addProfilePhotoView.setOnClickListener(v -> {
            outOfCard.setVisibility(View.VISIBLE);
            outOfCard.setScaleX(0);
            outOfCard.setScaleY(0);
            outOfCard.animate().scaleX(1).scaleY(1).setDuration(200);
            isBackGound = false;
        });

        addBackGroundPhotoView.setOnClickListener(v -> {
            outOfCard.setVisibility(View.VISIBLE);
            outOfCard.setScaleX(0);
            outOfCard.setScaleY(0);
            outOfCard.animate().scaleX(1).scaleY(1).setDuration(200);
            isBackGound = true;
        });

        selectFromCamera.setOnClickListener(v -> {
            Intent cropImage = new Intent(getContext(), CropImageActivity.class);
            cropImage.putExtra("is_camera", true);
            cropImage.putExtra("is_back_ground", isBackGound);
            startActivity(cropImage);
        });

        selectFromGallery.setOnClickListener(v -> {
            Intent cropImage = new Intent(getContext(), CropImageActivity.class);
            cropImage.putExtra("is_camera", false);
            cropImage.putExtra("is_back_ground", isBackGound);
            startActivity(cropImage);
        });

        outOfCard.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    outOfCard.animate().scaleX(0).scaleY(0).setDuration(200).withEndAction(() -> {
                        outOfCard.setVisibility(View.GONE);
                    });
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            return true;
        });

        return view;
    }

    void goToConversationWithUser(User user){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/get_or_create_chat/" + user.id + "/");
        try {
            String response = process.execute().get();
            if (response != null) {
                ChatObject chat = new ChatObject(new JsonParser().parse(response).getAsJsonObject());
                goToConversationActivity(chat);
            }
         } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void goToConversationActivity(ChatObject chat){
        Intent k = new Intent(getContext(), ConversationActivity.class);
        k.putExtra("chat", chat);
        startActivity(k);
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
                                            goToConversationActivityFirebase(chat);
                                        } else {
                                            createNewChatAndGoToIt(email);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    } else {
                        goToConversationActivityFirebase(chat);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
    }

    void goToConversationActivityFirebase(ChatFirebaseObject chat){
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

                    goToConversationActivityFirebase(chat);
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

    class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            UserPostFragment userPostFragment = new UserPostFragment();
            Bundle bundle = new Bundle();
            if (user != null && user.id != MainActivity.user.id) {
                bundle.putSerializable("user", user);
            } else {
                bundle.putSerializable("user", MainActivity.user);
            }
            userPostFragment.setArguments(bundle);
            Fragment[] tabFragment = {userPostFragment, new OptionsFragment(), new OptionsFragment()};
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"posts", "posts & reviews", "photos"};
            return titles[position];
        }
    }

}