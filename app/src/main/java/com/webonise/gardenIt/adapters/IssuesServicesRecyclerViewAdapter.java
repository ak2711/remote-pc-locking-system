package com.webonise.gardenIt.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.models.IssuesListModel;
import com.webonise.gardenIt.models.ServiceListModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DateUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.viewholders.IssuesRequestsViewHolder;

import java.util.List;

public class IssuesServicesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView
        .ViewHolder> {

    private List<IssuesListModel.Issues> issues;
    private List<ServiceListModel.Requests> requests;
    private SharedPreferenceManager sharedPreferenceManager;
    private DisplayImageOptions options;
    private Context context;
    private int type;

    public IssuesServicesRecyclerViewAdapter(Context context, int type) {
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
        this.type = type;
        if (type == Constants.CREATE_ISSUE) {
            IssuesListModel issuesListModel =
                    sharedPreferenceManager.getObject(Constants.KEY_PREF_USER_ISSUES,
                            IssuesListModel.class);

            issues = issuesListModel.getIssues();
        } else {
            ServiceListModel serviceListModel =
                    sharedPreferenceManager.getObject(Constants.KEY_PREF_USER_REQUEST,
                            ServiceListModel.class);
            requests = serviceListModel.getRequests();
        }
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
        viewHolder = new IssuesRequestsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IssuesRequestsViewHolder issuesRequestsViewHolder = (IssuesRequestsViewHolder) holder;
        configureDashboardPlantsViewHolder(issuesRequestsViewHolder, position);
    }

    @Override
    public int getItemCount() {
        return type == Constants.CREATE_ISSUE ? issues.size() : requests.size();
    }

    private void configureDashboardPlantsViewHolder(
            IssuesRequestsViewHolder issuesRequestsViewHolder, int position) {

        if (type == Constants.CREATE_ISSUE) {
            IssuesListModel.Issues issue = issues.get(position);
            issuesRequestsViewHolder.getTvTitle().setText(issue.getTitle());
            issuesRequestsViewHolder.getTvDescription().setText(issue.getDescription());
            ImageView imageView = issuesRequestsViewHolder.getImageView();
            ImageLoader.getInstance().displayImage(
                    Constants.BASE_URL + issue.getImages().get(0).getImage().getUrl(),
                    imageView, options, null);
            issuesRequestsViewHolder.getTvDate().setText(new DateUtil()
                    .getFormattedDateFromTimeStamp(issue.getCreatedAt()));
        } else {
            ServiceListModel.Requests request = requests.get(position);
            issuesRequestsViewHolder.getTvTitle().setText(request.getTitle());
            issuesRequestsViewHolder.getTvDescription().setText(request.getDescription());
            ImageView imageView = issuesRequestsViewHolder.getImageView();
            ImageLoader.getInstance().displayImage(
                    Constants.BASE_URL + request.getImages().get(0).getImage().getUrl(),
                    imageView, options, null);
            issuesRequestsViewHolder.getTvDate().setText(new DateUtil()
                    .getFormattedDateFromTimeStamp(request.getCreatedAt()));
        }
    }
}
