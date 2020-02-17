package com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class MyFoodCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodCommentRecyclerViewAdapter.ViewHolder> {

    Context context;
    private final List<FoodCommentObject> mValues;
    private final OnListFragmentInteractionListener mListener;
    private FoodCommentObject comment;

    public HashMap<Integer, MyFoodCommentRecyclerViewAdapter> adapters = new HashMap<>();
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    public MyFoodCommentRecyclerViewAdapter(List<FoodCommentObject> items, OnListFragmentInteractionListener listener, FoodCommentObject comment) {
        mValues = items;
        mListener = listener;
        this.comment = comment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_foodcomment, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = viewHolder.mView.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.wholeView.setVisibility(View.VISIBLE);
        holder.moreInList.setVisibility(View.GONE);

        if (holder.mItem.replies.size() > 0){
            holder.replyList.setVisibility(View.VISIBLE);
            holder.replyList.setLayoutManager(new LinearLayoutManager(holder.mView.getContext()));
            MyFoodCommentRecyclerViewAdapter adapter = new MyFoodCommentRecyclerViewAdapter(holder.mItem.replies, mListener, holder.mItem);
            adapters.put(holder.mItem.id, adapter);
            holder.replyList.setAdapter(adapter);
        } else {
            holder.replyList.setVisibility(View.GONE);
        }

        if (holder.mItem.is_max_depth){
            holder.continueConversation.setVisibility(View.VISIBLE);
            holder.continueConversation.setOnClickListener(v -> mListener.continueConversation(holder.mItem));
        } else {
            holder.continueConversation.setVisibility(View.GONE);
        }
        if (holder.mItem.is_last){
            holder.moreInList.setVisibility(View.VISIBLE);
            holder.moreInList.setOnClickListener(v -> {
                getMoreComments();
                holder.mItem.is_last = false;
            });
        } else {
            holder.moreInList.setVisibility(View.GONE);
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

    boolean anyTaskRunning(){
        for (AsyncTask task: tasks){
            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING){
                return true;
            }
        }
        return false;
    }
    int page = 2;
    public void getMoreComments(){
        if (!anyTaskRunning()){
            tasks.add(new GetCommentAsyncTask(context.getString(R.string.server) + "/comment_comments/" + this.comment.id + "/" + page + "/").execute());
        }
    }
    class GetCommentAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetCommentAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(context, this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                for (JsonElement je: ja){
                    FoodCommentObject moreComment = new FoodCommentObject(je.getAsJsonObject());
                    comment.replies.add(moreComment);
                    comment.repliesHashMap.put(moreComment.id, moreComment);
                }
                comment.replies.get(comment.replies.size() - 1).is_last = true;
                notifyDataSetChanged();
                page ++;
            }
            super.onPostExecute(response);
        }
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
        public final LinearLayout wholeView;
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
        public final TextView moreInList;
        public final TextView continueConversation;

        public final ImageButton optionsReply;
        public final RecyclerView replyList;
        public FoodCommentObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            wholeView = view.findViewById(R.id.whole_view);
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
            moreInList = view.findViewById(R.id.more_in_list);
            continueConversation = view.findViewById(R.id.continue_conversation);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onCommentCreate(FoodCommentObject comment);
        void onCommentDelete(FoodCommentObject comment);
        void onVoteComment(FoodCommentObject comment, boolean is_up_vote);
        void onDeleteVoteComment(FoodCommentObject comment);
        void continueConversation(FoodCommentObject comment);
        void onGoToProfile(User user);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
