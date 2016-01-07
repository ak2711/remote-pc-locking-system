package com.webonise.gardenIt.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.models.IssuesListModel;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.viewholders.DashboardPlantsViewHolder;
import com.webonise.gardenIt.viewholders.IssuesViewHolder;

import java.util.List;

public class IssuesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<IssuesListModel.Issues> issues;
    private SharedPreferenceManager sharedPreferenceManager;
    private DisplayImageOptions options;
    private Context context;

    public IssuesRecyclerViewAdapter(Context context) {
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
        IssuesListModel issuesListModel =
                sharedPreferenceManager.getObject(Constants.KEY_PREF_USER_ISSUES,
                        IssuesListModel.class);

        issues = issuesListModel.getIssues();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        AppController.getInstance().setupUniversalImageLoader(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =
                inflater.inflate(R.layout.image_text_list_item, parent, false);
        viewHolder = new IssuesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IssuesViewHolder issuesViewHolder = (IssuesViewHolder)holder;
        configureDashboardPlantsViewHolder(issuesViewHolder, position);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    private void configureDashboardPlantsViewHolder(
            IssuesViewHolder issuesViewHolder, int position){

        IssuesListModel.Issues issue = issues.get(position);
        issuesViewHolder.getTvTitle().setText(issue.getTitle());
        issuesViewHolder.getTvDescription().setText(issue.getDescription());
        ImageView imageView = issuesViewHolder.getImageView();
        ImageLoader.getInstance().displayImage(
                Constants.BASE_URL+issue.getImages().get(0).getImage().getUrl(),
                imageView, options, null);
    }
}
