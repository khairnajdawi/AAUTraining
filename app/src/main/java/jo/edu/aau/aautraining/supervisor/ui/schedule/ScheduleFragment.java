package jo.edu.aau.aautraining.supervisor.ui.schedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.supervisor.SupervisorMainActivity;
import jo.edu.aau.aautraining.supervisor.ui.students.StudentModel;
import jo.edu.aau.aautraining.trainer.TrainersMainActivity;
import jo.edu.aau.aautraining.trainer.ui.trainee.TraineeModel;

public class ScheduleFragment extends MyFragment implements CalendarPickerController {

    private ScheduleViewModel scheduleViewModel;
    private AgendaCalendarView mAgendaCalendarView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        scheduleViewModel =
                ViewModelProviders.of(this).get(ScheduleViewModel.class);
        View root = inflater.inflate(R.layout.supervisor_schedule_fragment, container, false);
        mAgendaCalendarView = root.findViewById(R.id.agenda_calendar_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTrainerSchedule();
    }

    private void getTrainerSchedule() {
        showProgressView();
        List<CalendarEvent> eventList = new ArrayList<>();
        SupervisorMainActivity activity = (SupervisorMainActivity) getActivity();
        int supervisorId = activity.getSupervisorId();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_SUPERVISOR_SCHEDULE + "?supervisor_id=" + supervisorId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                            String response = activity.handleApiResponse(s);
                            JSONArray trainingScheduleJsonArray = new JSONArray(response);
                            for (int i = 0; i < trainingScheduleJsonArray.length(); i++) {
                                JSONObject tsJsonObject = trainingScheduleJsonArray.getJSONObject(i);
                                String trainingPlace = "AAU Office" + " @" + tsJsonObject.getString("sch_time");
                                String trainingDate = tsJsonObject.getString("sch_date");
                                String studentName = tsJsonObject.getString("student_name");
                                String scheduleId = tsJsonObject.getString("schedule_id");
                                Date date = sdf.parse(trainingDate);
                                Calendar startTime1 = Calendar.getInstance();
                                startTime1.setTime(date);
                                Calendar endTime1 = Calendar.getInstance();
                                endTime1.setTime(date);
                                BaseCalendarEvent event1 = new BaseCalendarEvent(studentName, "Training Day", trainingPlace,
                                        ContextCompat.getColor(getContext(), R.color.colorPrimary), startTime1, endTime1, true);
                                event1.setId(Long.parseLong(scheduleId));

                                eventList.add(event1);
                            }

                            Calendar minDate = Calendar.getInstance();
                            Calendar maxDate = Calendar.getInstance();
                            minDate.add(Calendar.YEAR, -1);
                            minDate.set(Calendar.DAY_OF_MONTH, 1);
                            maxDate.add(Calendar.YEAR, 1);
                            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), ScheduleFragment.this);
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            activity.showSnackbar(R.string.error);
                        }

                        hideProgressView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                activity.showSnackbar(R.string.error);
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(getContext());
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        activity.addVolleyStringRequest(stringRequest);

    }

    @Override
    public void onDaySelected(DayItem dayItem) {
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        long scheduleId = event.getId();
        String traineeName = event.getTitle();
        Bundle bundle = new Bundle();
        bundle.putString("student_name", traineeName);
        bundle.putInt("schedule_id", (int) scheduleId);
        Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_schedule_to_supervisor_nav_edit_schedule, bundle).onClick(mAgendaCalendarView);
    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(1, 1, 1, getResources().getString(R.string.add)).setIcon(R.drawable.calendar_add_48)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            selectStudentForScheduleAdd();
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectStudentForScheduleAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_student);
        SupervisorMainActivity activity = (SupervisorMainActivity) getActivity();
        List<StudentModel> studentsList = activity.getStudentsList();
        List<String> studentsNamesList = new ArrayList<>();
        for (int i = 0; i < studentsList.size(); i++) {
            StudentModel studentModel = studentsList.get(i);
            studentsNamesList.add(studentModel.getStudentName().getValue());
        }
        builder.setItems(studentsNamesList.toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                StudentModel studentModel = studentsList.get(item);
                Bundle bundle = new Bundle();
                bundle.putString("student_name", studentModel.getStudentName().getValue());
                bundle.putInt("training_id", studentModel.getTrainingId().getValue());
                Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_schedule_to_supervisor_nav_add_schedule, bundle).onClick(mAgendaCalendarView);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}