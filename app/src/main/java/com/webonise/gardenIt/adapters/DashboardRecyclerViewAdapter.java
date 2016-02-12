package com.webonise.gardenIt.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.viewholders.DashboardPlantsViewHolder;

import java.util.List;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserDashboardModel.User.Gardens.Plants> plantsList;
    private SharedPreferenceManager sharedPreferenceManager;
    private DisplayImageOptions options;
    private Context context;

    public DashboardRecyclerViewAdapter(Context context) {
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
        UserDashboardModel userDashboardModel =
                sharedPreferenceManager.getObject(Constants.KEY_PREF_USER_GARDEN_PLANTS,
                        UserDashboardModel.class);

        List<UserDashboardModel.User.Gardens> gardensList = userDashboardModel.getUser()
                .getGardens();
        UserDashboardModel.User.Gardens gardens = gardensList.get(gardensList.size() - 1);
        plantsList = gardens.getPlants();
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
        DashboardPlantsViewHolder dashboardPlantsViewHolder = (DashboardPlantsViewHolder)holder;
        configureDashboardPlantsViewHolder(dashboardPlantsViewHolder, position);
    }

    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    private void configureDashboardPlantsViewHolder(
            DashboardPlantsViewHolder dashboardPlantsViewHolder, final int position){

        UserDashboardModel.User.Gardens.Plants plants = plantsList.get(position);
        dashboardPlantsViewHolder.getTvTitle().setText(plants.getName());
        dashboardPlantsViewHolder.getTvDescription().setText(plants.getDescription());
        ImageView ivPlant = dashboardPlantsViewHolder.getIvPlant();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                new DisplayUtil(context).getImageHeight());
        ivPlant.setLayoutParams(params);
        ImageLoader.getInstance().displayImage(
                Constants.BASE_URL+plants.getImages().get(0).getImage().getUrl(),
                ivPlant, options, null);

        dashboardPlantsViewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlantDetailsActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_POSITION, position);
                context.startActivity(intent);
            }
        });
    }
}
