package com.webonise.gardenIt.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.activities.PlantDetailsActivity;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DateUtil;
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.viewholders.DashboardPlantsViewHolder;

import java.util.List;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserDashboardModel.User.Gardens.Plants> plantsList;
    private SharedPreferenceManager sharedPreferenceManager;
    private DisplayImageOptions options;
    private Context context;

    public DashboardRecyclerViewAdapter(Context context,
                                        List<UserDashboardModel.User.Gardens> allGardens,
                                        int gardenId) {
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
        UserDashboardModel.User.Gardens gardens = null;
        if (allGardens != null && allGardens.size() > 0) {
            for (int i = 0; i < allGardens.size(); i++) {
                if (gardenId == allGardens.get(i).getId()) {
                    gardens = allGardens.get(i);
                }
            }

            plantsList = gardens.getPlants();
        }
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        AppController.getInstance().setupUniversalImageLoader(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =
                inflater.inflate(R.layout.grid_item_dashboard, parent, false);
        viewHolder = new DashboardPlantsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DashboardPlantsViewHolder dashboardPlantsViewHolder = (DashboardPlantsViewHolder) holder;
        configureDashboardPlantsViewHolder(dashboardPlantsViewHolder, position);
    }

    @Override
    public int getItemCount() {
        return plantsList != null ? plantsList.size() : 0;
    }

    private void configureDashboardPlantsViewHolder(
            DashboardPlantsViewHolder dashboardPlantsViewHolder, int position) {

        final UserDashboardModel.User.Gardens.Plants plants = plantsList.get(position);
        dashboardPlantsViewHolder.getTvTitle().setText(plants.getName());
        dashboardPlantsViewHolder.getTvDescription().setText(plants.getDescription());
        dashboardPlantsViewHolder.getTvDate().setText(DateUtil.getFormattedDateFromTimeStamp(
                plants.getUpdatedAt(), DateUtil.DATE_FORMAT_DD_MMM_YYYY_HH_MM));
        ImageView ivPlant = dashboardPlantsViewHolder.getIvPlant();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                new DisplayUtil(context).getImageHeight());
        ivPlant.setLayoutParams(params);
        ImageLoader.getInstance().displayImage(
                Constants.BASE_URL + plants.getImages().get(0).getImage().getUrl(),
                ivPlant, options, null);

        dashboardPlantsViewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlantDetailsActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_PLANT_ID, plants.getId());
                context.startActivity(intent);
            }
        });
    }
}
