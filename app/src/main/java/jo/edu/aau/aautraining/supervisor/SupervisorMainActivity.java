package jo.edu.aau.aautraining.supervisor;

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
import jo.edu.aau.aautraining.supervisor.ui.students.StudentModel;

import static androidx.annotation.Dimension.SP;

public class SupervisorMainActivity extends MyAppCompatActivity {

    private NavController navController;
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private int supervisorId;
    private List<StudentModel> studentsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        supervisorId = extras.getInt("supervisor_id");
        String imageLink = extras.getString("img_link");
        String fullName = extras.getString("full_name");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_main);

        Toolbar toolbar = findViewById(R.id.supervisor_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.supervisor_drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.supervisor_nav_profile)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        NavDestination chatListDestination = navController.getGraph().findNode(R.id.supervisor_nav_chat_list);
        NavArgument.Builder builder4 = new NavArgument.Builder();
        chatListDestination.addArgument("from_id", builder4
                .setType(NavType.IntType)
                .setDefaultValue(supervisorId)
                .build());
        chatListDestination.addArgument("from_role", builder4
                .setType(NavType.StringType)
                .setDefaultValue("Supervisor")
                .build());

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        TextView navHeaderNameTextView = navigationView.getHeaderView(0).findViewById(R.id.supervisor_navheader_name);
        navHeaderNameTextView.setText(fullName);

        NetworkImageView headerImageView = navigationView.getHeaderView(0).findViewById(R.id.supervisor_navheader_imageview);
        if (!(imageLink == null) && !imageLink.isEmpty()) {
            headerImageView.setImageUrl(AppConstants.APP_BASE_URL + imageLink, getImageLoader());
            headerImageView.setErrorImageResId(R.drawable.training);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getStudentsListFromAPI();
        getHasNotifications();
    }

    public void getHasNotifications() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.GET_HAS_NOTIFICATION + "?to_role=supervisor&to_id=" + supervisorId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = handleApiResponse(response);
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            TextView menuItem = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.supervisor_nav_notification_list));
                            menuItem.setGravity(Gravity.CENTER_VERTICAL);
                            menuItem.setTypeface(null, Typeface.BOLD);
                            menuItem.setTextSize(SP, 20f);
                            menuItem.setTextColor(getResources().getColor(R.color.colorPrimary));
                            if (jsonObject.getBoolean("has_notification")) {
                                menuItem.setText("!");
                            } else {
                                menuItem.setText("");
                            }

                            TextView menuItem2 = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.supervisor_nav_chat_list));
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
                MySharedPreference mySharedPreference = new MySharedPreference(SupervisorMainActivity.this);
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

    public int getSupervisorId() {
        return this.supervisorId;
    }

    private void getStudentsListFromAPI() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_SUPERVISOR_STUDENT_LIST + "?supervisor_id=" + supervisorId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("TraineeList API : " + supervisorId + " : " + s);
                        try {
                            String response = handleApiResponse(s);
                            JSONArray traineeJsonArray = new JSONArray(response);
                            studentsList = new ArrayList<>();
                            for (int i = 0; i < traineeJsonArray.length(); i++) {
                                JSONObject traineeJsonObject = traineeJsonArray.getJSONObject(i);
                                StudentModel studentModel = new StudentModel();
                                studentModel.setStudentId(traineeJsonObject.getInt("student_id"));
                                studentModel.setStudentName(traineeJsonObject.getString("student_name"));
                                studentModel.setStudentImageLink(traineeJsonObject.getString("img_link"));
                                studentModel.setTrainingId(traineeJsonObject.getInt("training_id"));
                                studentModel.setTrainingStatus(traineeJsonObject.getInt("training_status"));
                                studentsList.add(studentModel);
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
                MySharedPreference mySharedPreference = new MySharedPreference(SupervisorMainActivity.this);
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);

    }

    public List<StudentModel> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(List<StudentModel> studentsList) {
        this.studentsList = studentsList;
    }
}