package com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;

import java.util.List;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class MyFoodCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodCommentRecyclerViewAdapter.ViewHolder> {

    private final List<FoodCommentObject> mValues;
    private final OnListFragmentInteractionListener mListener;
    MyFoodCommentRecyclerViewAdapter adapter;


    public MyFoodCommentRecyclerViewAdapter(List<FoodCommentObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_foodcomment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (holder.mItem.replies.size() > 0){
            holder.replyList.setVisibility(View.VISIBLE);
            holder.replyList.setLayoutManager(new LinearLayoutManager(holder.mView.getContext()));
            adapter = new MyFoodCommentRecyclerViewAdapter(holder.mItem.replies, mListener);
            holder.replyList.setAdapter(adapter);
        } else {
            holder.replyList.setVisibility(View.GONE);
        }

        holder.mItem = mValues.get(position);
        holder.reviewerName.setText(holder.mItem.owner.first_name + ", " + holder.mItem.owner.last_name);
        holder.reviewerUsername.setText(holder.mItem.owner.username);
        holder.review.setText(holder.mItem.message);
        holder.votes.setText(holder.mItem.votes_n + "");

        holder.votes.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
        holder.upVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.upvote));
        holder.downVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.downvote));

        if (holder.mItem.is_user_up_vote != null){
            holder.votes.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorSecondary));
            if (holder.mItem.is_user_up_vote){
                holder.upVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.upvoted));
            } else {
                holder.downVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.downvoted));
            }
        }

        if(!holder.mItem.owner.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.reviewerImage);
        } else {
            holder.reviewerImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }

        if (holder.mItem.owner.id == USER.id){
            holder.replyComment.setVisibility(View.GONE);
        } else {
            holder.replyComment.setVisibility(View.VISIBLE);
            holder.replyComment.setOnClickListener(v -> {
                mListener.onCommentCreate(holder.mItem);
            });
        }


        holder.reviewerImage.setOnClickListener(v -> mListener.onGoToProfile(holder.mItem.owner));
        holder.upVote.setOnClickListener(v -> {
            if (holder.mItem.is_user_up_vote == null){
                mListener.onVoteComment(holder.mItem, true);
            } else {
                if (holder.mItem.is_user_up_vote){
                    mListener.onDeleteVoteComment(holder.mItem);
                } else {
                    mListener.onVoteComment(holder.mItem, true);
                }
            }
        });
        holder.downVote.setOnClickListener(v -> {
            if (holder.mItem.is_user_up_vote == null){
                mListener.onVoteComment(holder.mItem, false);
            } else {
                if (holder.mItem.is_user_up_vote){
                    mListener.onVoteComment(holder.mItem, false);
                } else {
                    mListener.onDeleteVoteComment(holder.mItem);
                }
            }
        });
        setOptionsMenu(holder);
    }

    void setOptionsMenu(MyFoodCommentRecyclerViewAdapter.ViewHolder holder){
        holder.optionsReview.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.mView.getContext(), holder.optionsReview);
            if (holder.mItem.owner.id != USER.id){
                popupMenu.getMenu().add("Reply");
                popupMenu.getMenu().add("Report");
            } else {
                popupMenu.getMenu().add("Delete");
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(holder, item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
    }

    void setOptionsActions(MyFoodCommentRecyclerViewAdapter.ViewHolder holder, String title){
        switch (title){
            case "Reply":
                mListener.onCommentCreate(holder.mItem);
                break;
            case "Delete":
                mListener.onCommentDelete(holder.mItem);
                break;
            case "Report":
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView reviewerImage;
        public final ImageView replyerImage;
        public final TextView reviewerName;
        public final TextView reviewerUsername;
        public final TextView review;
        public final ImageView optionsReview;
        public final ImageView replyComment;
        public final ImageView upVote;
        public final TextView votes;
        public final ImageView downVote;

        public final TextView replyerName;
        public final TextView replyerUsername;
        public final TextView reply;
        public final ImageButton optionsReply;
        public final RecyclerView replyList;
        public FoodCommentObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            reviewerImage = view.findViewById(R.id.reviewer_image);
            replyerImage = view.findViewById(R.id.reviewer_image_ans);
            reviewerName = view.findViewById(R.id.reviewer_name);
            reviewerUsername = view.findViewById(R.id.reviewer_username);
            review = view.findViewById(R.id.review);
            optionsReview = view.findViewById(R.id.options_review);
            replyerName = view.findViewById(R.id.reviewer_name_ans);
            replyerUsername = view.findViewById(R.id.reviewer_username_ans);
            reply = view.findViewById(R.id.reply);
            optionsReply = view.findViewById(R.id.options_review_reply);
            replyList = view.findViewById(R.id.reply_list);
            replyComment = view.findViewById(R.id.reply_comment);
            upVote = view.findViewById(R.id.upvote);
            votes = view.findViewById(R.id.votes);
            downVote = view.findViewById(R.id.downvote);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onCommentCreate(FoodCommentObject comment);
        void onCommentDelete(FoodCommentObject comment);
        void onVoteComment(FoodCommentObject comment, boolean is_up_vote);
        void onDeleteVoteComment(FoodCommentObject comment);
        void onGoToProfile(User user);
    }
}
