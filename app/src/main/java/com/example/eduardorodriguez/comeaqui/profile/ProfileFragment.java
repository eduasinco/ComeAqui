package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eduardorodriguez.comeaqui.LoginActivity;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.EditProfileActivity;
import com.example.eduardorodriguez.comeaqui.profile.post_and_reviews.PostAndReviewsFragment;
import com.example.eduardorodriguez.comeaqui.profile.settings.SettingsActivity;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;
import com.example.eduardorodriguez.comeaqui.utilities.ProfileImageGalleryFragment;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;
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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;
import static com.example.eduardorodriguez.comeaqui.MainActivity.firebaseUser;

public class ProfileFragment extends Fragment implements SelectImageFromFragment.OnFragmentInteractionListener{

    public static String[] data;
    public View view;
    boolean isBackGound;
    private User user;

    private ImageView profileImageView;
    private ImageView backGroundImage;
    private ImageView messageImage;
    private ImageView editProfileView;
    private ImageView addProfilePhotoView;
    private ImageView addBackGroundPhotoView;

    private TextView emailView;
    private TextView bioView;
    private TextView nameView;

    private ImageButton settingsButton;

    private ConstraintLayout outOfCard;
    FrameLayout fragmentView;
    private static final String USER_TO_DISPLAY = "user";

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_TO_DISPLAY, user);
        fragment.setArguments(args);
        return fragment;
    }


    public void setProfile(User user){
        fragmentView.setVisibility(View.GONE);
        settingsButton.setVisibility(View.GONE);
        if (user.id == USER.id){
            settingsButton.setVisibility(View.VISIBLE);
            setSettingsButton();
            editProfileView.setVisibility(View.VISIBLE);
            editProfileView.setOnClickListener(v -> {
                Intent editProfile = new Intent(getContext(), EditProfileActivity.class);
                editProfile.putExtra("object", user);
                getContext().startActivity(editProfile);
            });
            addProfilePhotoView.setVisibility(View.VISIBLE);
            addBackGroundPhotoView.setVisibility(View.VISIBLE);
        } else {
            messageImage.setVisibility(View.VISIBLE);
            messageImage.setOnClickListener(v -> goToConversationWithUser(user));
        }

        nameView.setText(user.first_name + " " + user.last_name);
        emailView.setText(user.email);
        if (user.bio != null && !user.bio.equals("")) {
            bioView.setVisibility(View.VISIBLE);
            bioView.setText(user.bio);
        }

        if(!user.profile_photo.contains("no-image")) {
            Glide.with(view.getContext()).load(user.profile_photo).into(profileImageView);
            profileImageView.setOnClickListener((v) -> {
                Intent imageLook = new Intent(getContext(), ImageLookActivity.class);
                imageLook.putExtra("image_url", user.profile_photo);
                getContext().startActivity(imageLook);
            });
        }
        if(!user.background_photo.contains("no-image")) {
            Glide.with(view.getContext()).load(user.background_photo).into(backGroundImage);
            backGroundImage.setOnClickListener((v) -> {
                Intent imageLook = new Intent(getContext(), ImageLookActivity.class);
                imageLook.putExtra("image_url", user.background_photo);
                getContext().startActivity(imageLook);
            });
        }

        int curveRadius = 40;
        backGroundImage.setClipToOutline(true);
        backGroundImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight() + curveRadius), curveRadius);
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(user.rating, user.ratingN))
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER_TO_DISPLAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView backGroundImageView = view.findViewById(R.id.backGroundImage);

        profileImageView = view.findViewById(R.id.profile_image);
        backGroundImage = view.findViewById(R.id.backGroundImage);
        messageImage = view.findViewById(R.id.message);
        emailView = view.findViewById(R.id.senderEmail);
        bioView = view.findViewById(R.id.bioView);
        nameView = view.findViewById(R.id.nameView);
        editProfileView = view.findViewById(R.id.edit_profile);
        addProfilePhotoView = view.findViewById(R.id.add_profile_photo);
        addBackGroundPhotoView = view.findViewById(R.id.add_background_photo);
        fragmentView = view.findViewById(R.id.select_from);
        settingsButton = view.findViewById(R.id.settings_profile_button);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        addProfilePhotoView.setOnClickListener(v -> {
            isBackGound = false;
            fragmentView.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.select_from, SelectImageFromFragment.newInstance(true))
                    .commit();
        });

        addBackGroundPhotoView.setOnClickListener(v -> {
            isBackGound = true;
            fragmentView.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.select_from, SelectImageFromFragment.newInstance(true))
                    .commit();
        });

        setProfile(user);

        return view;
    }

    void setSettingsButton(){
        settingsButton.setOnClickListener(v -> {
            signOut();
        });
    }

    private void signOut(){
        SharedPreferences pref = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("signed_in", false);
        edt.remove("email");
        edt.remove("password");
        edt.apply();

        Intent bactToLogin = new Intent(getActivity(), LoginActivity.class);
        startActivity(bactToLogin);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        saveProfileImage(uri);
    }


    private void saveProfileImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), imageUri);
            if (bitmap != null){
                PatchAsyncTask putTask = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
                putTask.imageBitmap = bitmap;
                if (isBackGound){
                    putTask.execute("background_photo", "", "true").get(15, TimeUnit.SECONDS);
                }else {
                    putTask.execute("profile_photo", "", "true").get(15, TimeUnit.SECONDS);
                }
            }
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment[] tabFragment = {
                    UserPostFragment.newInstance(user),
                    PostAndReviewsFragment.newInstance(user),
                    ProfileImageGalleryFragment.newInstance(user)
            };
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"posts", "posts & reviews", "media"};
            return titles[position];
        }
    }

}