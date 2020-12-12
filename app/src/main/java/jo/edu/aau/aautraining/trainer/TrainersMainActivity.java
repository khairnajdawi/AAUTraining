package jo.edu.aau.aautraining.trainer;


import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
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
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;

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
                R.id.trainer_nav_profile, R.id.trainer_nav_schedule, R.id.trainer_nav_trainee_list, R.id.trainer_nav_logout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //navController.setGraph(navController.getGraph(), getIntent().getExtras());

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navHeaderNameTextView = navigationView.getHeaderView(0).findViewById(R.id.trainer_navheader_name);
        String fullName = getIntent().getExtras().getString("full_name");
        navHeaderNameTextView.setText(fullName);

        headerImageView = navigationView.getHeaderView(0).findViewById(R.id.trainer_navheader_imageview);
        String imageLink = getIntent().getExtras().getString("img_link");
        if (!(imageLink == null) && !imageLink.isEmpty()) {
            headerImageView.setImageUrl(AppConstants.API_BASE_URL + imageLink, getImageLoader());
            headerImageView.setErrorImageResId(R.drawable.training);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getTraineesListVolley();
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
