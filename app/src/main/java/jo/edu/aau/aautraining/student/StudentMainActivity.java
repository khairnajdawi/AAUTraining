package jo.edu.aau.aautraining.student;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavType;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;

public class StudentMainActivity extends MyAppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView navHeaderNameTextView;
    private NavController navController;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeaderNameTextView = navigationView.getHeaderView(0).findViewById(R.id.student_navheader_name);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.student_nav_profile, R.id.student_nav_schedule, R.id.student_nav_contact_supervisor, R.id.student_nav_contact_trainer, R.id.student_nav_logout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int supervisorId = extras.getInt("supervisorId");
            int studentId = extras.getInt("studentId");
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
            contactSupervisorDestination.addArgument("supervisorId", builder2
                    .setType(NavType.IntType)
                    .setDefaultValue(supervisorId)
                    .build());

            NavDestination contactTrainerDestination = navController.getGraph().findNode(R.id.student_nav_contact_trainer);
            NavArgument.Builder builder3 = new NavArgument.Builder();
            contactTrainerDestination.addArgument("trainerId", builder3
                    .setType(NavType.IntType)
                    .setDefaultValue(trainerId)
                    .build());

            NavDestination scheduleNavDestination = navController.getGraph().findNode(R.id.student_nav_schedule);
            NavArgument.Builder builder4 = new NavArgument.Builder();
            scheduleNavDestination.addArgument("trainingId", builder4
                    .setType(NavType.IntType)
                    .setDefaultValue(trainingId)
                    .build());

        }
    }

    public void setHeaderName(String name) {
        navHeaderNameTextView.setText(name);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}