package com.g05.itkmitl.multioder.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g05.itkmitl.multioder.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHoler> {
    private List<Comment> mComments;
    private Context mContext;

    public static class ViewHoler extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView userName;
        public TextView message;
        public TextView date;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            message = itemView.findViewById(R.id.comment_message);
            date = itemView.findViewById(R.id.comment_date);
        }
    }

    public CommentAdapter(Context context, List<Comment> dataset) {
        this.mContext = context;
        this.mComments = dataset;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        ViewHoler viewHolder = new ViewHoler(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler viewHoler, int i) {
        Comment commentItem = mComments.get(i);

        viewHoler.userName.setText("user id");
        viewHoler.message.setText(commentItem.getMessage());
        viewHoler.date.setText(commentItem.getDate().toString());

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}
