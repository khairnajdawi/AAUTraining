package jo.edu.aau.aautraining.trainer;


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavType;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.supervisor.SupervisorMainActivity;
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;

import static androidx.annotation.Dimension.SP;

public class TrainersMainActivity extends MyAppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView navHeaderNameTextView;
    private NetworkImageView headerImageView;
    private NavController navController;
    private NavigationView navigationView;
    private int trainerId;
    private List<TraineeModel> traineesList;

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    @Override
    protected void onStart() {
        trainerId = getIntent().getExtras().getInt("trainer_id");
        setTrainerId(trainerId);
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trainer_main);
        Toolbar toolbar = findViewById(R.id.trainer_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.trainer_drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.trainer_nav_profile)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //navController.setGraph(navController.getGraph(), getIntent().getExtras());

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        trainerId = getIntent().getExtras().getInt("trainer_id");
        NavDestination chatListDestination = navController.getGraph().findNode(R.id.trainer_nav_chat_list);
        NavArgument.Builder builder4 = new NavArgument.Builder();
        chatListDestination.addArgument("from_id", builder4
                .setType(NavType.IntType)
                .setDefaultValue(trainerId)
                .build());
        chatListDestination.addArgument("from_role", builder4
                .setType(NavType.StringType)
                .setDefaultValue("Trainer")
                .build());

        NavDestination notificationsDestination = navController.getGraph().findNode(R.id.trainer_nav_notification_list);
        NavArgument.Builder builder6 = new NavArgument.Builder();
        notificationsDestination.addArgument("to_id", builder6
                .setType(NavType.IntType)
                .setDefaultValue(trainerId)
                .build());
        notificationsDestination.addArgument("to_role", builder6
                .setType(NavType.StringType)
                .setDefaultValue("trainer")
                .build());

        navHeaderNameTextView = navigationView.getHeaderView(0).findViewById(R.id.trainer_navheader_name);
        String fullName = getIntent().getExtras().getString("full_name");
        navHeaderNameTextView.setText(fullName);

        headerImageView = navigationView.getHeaderView(0).findViewById(R.id.trainer_navheader_imageview);
        String imageLink = getIntent().getExtras().getString("img_link");
        if (!(imageLink == null) && !imageLink.isEmpty()) {
            headerImageView.setImageUrl(AppConstants.APP_BASE_URL + imageLink, getImageLoader());
            headerImageView.setErrorImageResId(R.drawable.training);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getTraineesListVolley();
        getHasNotifications();
    }


    public void getHasNotifications() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.GET_HAS_NOTIFICATION + "?to_role=trainer&to_id=" + trainerId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = handleApiResponse(response);
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            TextView menuItem = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.trainer_nav_notification_list));
                            menuItem.setGravity(Gravity.CENTER_VERTICAL);
                            menuItem.setTypeface(null, Typeface.BOLD);
                            menuItem.setTextSize(SP, 20f);
                            menuItem.setTextColor(getResources().getColor(R.color.colorPrimary));
                            if (jsonObject.getBoolean("has_notification")) {
                                menuItem.setText("!");
                            } else {
                                menuItem.setText("");
                            }

                            TextView menuItem2 = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.trainer_nav_chat_list));
                            menuItem2.setGravity(Gravity.CENTER_VERTICAL);
                            menuItem2.setTypeface(null, Typeface.BOLD);
                            menuItem2.setTextSize(SP, 20f);
                            menuItem2.setTextColor(getResources().getColor(R.color.colorPrimary));
                            if (jsonObject.getBoolean("has_msg")) {
                                menuItem2.setText("!");
                            } else {
                                menuItem2.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackbar();
            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(TrainersMainActivity.this);
                String token = mySharedPreference.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void getTraineesListVolley() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_TRAINER_TRAINEE_LIST + "?trainer_id=" + trainerId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("TraineeList API : " + trainerId + " : " + s);
                        try {
                            String response = handleApiResponse(s);
                            JSONArray traineeJsonArray = new JSONArray(response);
                            traineesList = new ArrayList<>();
                            for (int i = 0; i < traineeJsonArray.length(); i++) {
                                JSONObject traineeJsonObject = traineeJsonArray.getJSONObject(i);
                                TraineeModel traineeModel = new TraineeModel();
                                traineeModel.setStudentId(traineeJsonObject.getInt("student_id"));
                                traineeModel.setStudentName(traineeJsonObject.getString("student_name"));
                                traineeModel.setStudentImageLink(traineeJsonObject.getString("img_link"));
                                traineeModel.setTrainingId(traineeJsonObject.getInt("training_id"));
                                traineeModel.setTrainingStatus(traineeJsonObject.getInt("training_status"));
                                traineesList.add(traineeModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(TrainersMainActivity.this);
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);

    }

    public List<TraineeModel> getTraineesList() {
        return traineesList;
    }

    public void setTraineesList(List<TraineeModel> traineesList) {
        this.traineesList = traineesList;
    }
}
