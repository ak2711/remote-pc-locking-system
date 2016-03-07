package com.webonise.gardenIt.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webonise.gardenIt.R;

public class IssuesRequestsViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = this.getClass().getName();

    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDate;
    private View view;
    private View statusView;

    public IssuesRequestsViewHolder(View view) {
        super(view);
        this.view = view;
        imageView = (ImageView) view.findViewById(R.id.imageView);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        statusView = view.findViewById(R.id.statusView);
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

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getStatusView() {
        return statusView;
    }

    public void setStatusView(View statusView) {
        this.statusView = statusView;
    }
}
