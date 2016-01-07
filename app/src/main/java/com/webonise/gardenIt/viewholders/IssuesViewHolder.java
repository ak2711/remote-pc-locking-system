package com.webonise.gardenIt.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webonise.gardenIt.R;

public class IssuesViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = this.getClass().getName();

    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDate;

    public IssuesViewHolder(View view) {
        super(view);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }

    public void setTvDescription(TextView tvDescription) {
        this.tvDescription = tvDescription;
    }

    public TextView getTvDate() {
        return tvDate;
    }

    public void setTvDate(TextView tvDate) {
        this.tvDate = tvDate;
    }
}
