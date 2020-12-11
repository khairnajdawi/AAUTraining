package jo.edu.aau.aautraining.student.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

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
import jo.edu.aau.aautraining.shared.MyAppCompatActivity;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class ScheduleFragment extends MyFragment implements CalendarPickerController {

    private ScheduleViewModel scheduleViewModel;
    private AgendaCalendarView mAgendaCalendarView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scheduleViewModel =
                ViewModelProviders.of(this).get(ScheduleViewModel.class);
        View root = inflater.inflate(R.layout.student_fragment_schedule, container, false);
        mAgendaCalendarView = root.findViewById(R.id.agenda_calendar_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            ScheduleFragmentArgs scheduleFragmentArgs = ScheduleFragmentArgs.fromBundle(getArguments());
            int trainingId = scheduleFragmentArgs.getTrainingId();
            getStudentSchedule(trainingId);
        }

    }

    private void getStudentSchedule(int trainingId) {
        showProgressView();
        List<CalendarEvent> eventList=new ArrayList<>();
        MyAppCompatActivity activity = (MyAppCompatActivity) getActivity();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.API_GET_STUDENT_SCHEDULE + "?training_id=" + trainingId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd",Locale.US);

                            String response = activity.handleApiResponse(s);
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray trainingScheduleJsonArray = jsonResponse.getJSONArray("training_schedule");
                            for(int i=0;i<trainingScheduleJsonArray.length();i++){
                                JSONObject tsJsonObject=trainingScheduleJsonArray.getJSONObject(i);
                                String trainingPlace=tsJsonObject.getString("training_place");
                                String trainingDate=tsJsonObject.getString("training_date");
                                Date date=sdf.parse(trainingDate);
                                Calendar startTime1 = Calendar.getInstance();
                                startTime1.setTime(date);
                                Calendar endTime1 = Calendar.getInstance();
                                endTime1.setTime(date);
                                BaseCalendarEvent event1 = new BaseCalendarEvent("Training", "Training Day", trainingPlace,
                                        ContextCompat.getColor(getContext(), R.color.colorPrimary), startTime1, endTime1, true);
                                eventList.add(event1);
                            }

                            JSONArray officeScheduleJsonArray=jsonResponse.getJSONArray("office_schedule");
                            for(int u=0;u<officeScheduleJsonArray.length();u++){
                                JSONObject osJsonObject=officeScheduleJsonArray.getJSONObject(u);
                                String osDate=osJsonObject.getString("sch_date");
                                String osTime=osJsonObject.getString("sch_time");
                                Date date2=sdf.parse(osDate);
                                Calendar startTime2 = Calendar.getInstance();
                                startTime2.setTime(date2);
                                Calendar endTime2 = Calendar.getInstance();
                                endTime2.setTime(date2);
                                BaseCalendarEvent event2 = new BaseCalendarEvent("Supervisor meeting", "Supervisor meeting", "AAU Office @"+osTime,
                                        ContextCompat.getColor(getContext(), R.color.colorAccent), startTime2, endTime2, false);
                                eventList.add(event2);

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
        Toast.makeText(getContext(), "Day selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        if(event!=null)
            Toast.makeText(getContext(), "Event "+event.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}