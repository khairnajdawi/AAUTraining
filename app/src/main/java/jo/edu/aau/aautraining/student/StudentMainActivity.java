package jo.edu.aau.aautraining.student;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.shared.ui.notifications.NotificationItem;

import static androidx.annotation.Dimension.SP;

public class StudentMainActivity extends MyAppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView navHeaderNameTextView;
    private NavController navController;
    private NavigationView navigationView;
    private int studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeaderNameTextView = navigationView.getHeaderView(0).findViewById(R.id.student_navheader_name);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.student_nav_profile)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int supervisorId = extras.getInt("supervisorId");
            studentId = extras.getInt("studentId");
            int trainerId = extras.getInt("trainerId");
            int trainingId = extras.getInt("trainingId");
            navController.setGraph(navController.getGraph(),extras);
            NavDestination profileDestination = navController.getGraph().findNode(R.id.student_nav_profile);
            NavArgument.Builder builder = new NavArgument.Builder();
            profileDestination.addArgument("studentId", builder
                    .setType(NavType.IntType)
                    .setDefaultValue(studentId)
                    .build());
            profileDestination.addArgument("trainingId", builder
                    .setType(NavType.IntType)
                    .setDefaultValue(trainingId)
                    .build());

            NavDestination contactSupervisorDestination = navController.getGraph().findNode(R.id.student_nav_contact_supervisor);
            NavArgument.Builder builder2 = new NavArgument.Builder();
            contactSupervisorDestination.addArgument("studentId", builder2
                    .setType(NavType.IntType)
                    .setDefaultValue(studentId)
                    .build());
            contactSupervisorDestination.addArgument("supervisorId", builder2
                    .setType(NavType.IntType)
                    .setDefaultValue(supervisorId)
                    .build());

            NavDestination contactTrainerDestination = navController.getGraph().findNode(R.id.student_nav_contact_trainer);
            NavArgument.Builder builder3 = new NavArgument.Builder();
            contactTrainerDestination.addArgument("studentId", builder3
                    .setType(NavType.IntType)
                    .setDefaultValue(studentId)
                    .build());
            contactTrainerDestination.addArgument("trainerId", builder3
                    .setType(NavType.IntType)
                    .setDefaultValue(trainerId)
                    .build());

            NavDestination chatListDestination = navController.getGraph().findNode(R.id.student_nav_chat_list);
            NavArgument.Builder builder4 = new NavArgument.Builder();
            chatListDestination.addArgument("from_id", builder4
                    .setType(NavType.IntType)
                    .setDefaultValue(studentId)
                    .build());
            chatListDestination.addArgument("from_role", builder4
                    .setType(NavType.StringType)
                    .setDefaultValue("Student")
                    .build());

            NavDestination scheduleNavDestination = navController.getGraph().findNode(R.id.student_nav_schedule);
            NavArgument.Builder builder5 = new NavArgument.Builder();
            scheduleNavDestination.addArgument("trainingId", builder5
                    .setType(NavType.IntType)
                    .setDefaultValue(trainingId)
                    .build());


            NavDestination notificationsDestination = navController.getGraph().findNode(R.id.student_nav_notification_list);
            NavArgument.Builder builder6 = new NavArgument.Builder();
            notificationsDestination.addArgument("to_id", builder6
                    .setType(NavType.IntType)
                    .setDefaultValue(studentId)
                    .build());
            notificationsDestination.addArgument("to_role", builder6
                    .setType(NavType.StringType)
                    .setDefaultValue("student")
                    .build());

        }

    }

    public void setHeaderName(String name) {
        navHeaderNameTextView.setText(name);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getHasNotifications();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void getHasNotifications() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.GET_HAS_NOTIFICATION + "?to_role=student&to_id=" + studentId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = handleApiResponse(response);
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            TextView menuItem = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.student_nav_notification_list));
                            menuItem.setGravity(Gravity.CENTER_VERTICAL);
                            menuItem.setTypeface(null, Typeface.BOLD);
                            menuItem.setTextSize(SP, 20f);
                            menuItem.setTextColor(getResources().getColor(R.color.colorPrimary));
                            if (jsonObject.getBoolean("has_notification")) {
                                menuItem.setText("!");
                            } else {
                                menuItem.setText("");
                            }

                            TextView menuItem2 = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.student_nav_chat_list));
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
                MySharedPreference mySharedPreference = new MySharedPreference(StudentMainActivity.this);
                String token = mySharedPreference.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }
}