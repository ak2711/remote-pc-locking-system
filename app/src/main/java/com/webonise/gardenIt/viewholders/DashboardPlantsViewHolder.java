package com.webonise.gardenIt.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.webonise.gardenIt.R;

import butterknife.ButterKnife;

public class DashboardPlantsViewHolder extends RecyclerView.ViewHolder {
    private final String TAG = this.getClass().getName();

    private ImageView ivPlant;
    private TextView tvTitle;
    private TextView tvDescription;
    private View view;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public ImageView getIvPlant() {
        return ivPlant;
    }

    public void setIvPlant(ImageView ivPlant) {
        this.ivPlant = ivPlant;
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

    public DashboardPlantsViewHolder(View view) {
        super(view);
        this.view = view;
        ivPlant = (ImageView) view.findViewById(R.id.ivPlant);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
    }
}
