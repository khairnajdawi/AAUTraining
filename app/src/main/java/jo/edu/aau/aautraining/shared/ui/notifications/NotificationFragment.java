package jo.edu.aau.aautraining.shared.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class NotificationFragment extends MyFragment {

    String userRole, userId;
    private NotificationViewModel mViewModel;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        View root = inflater.inflate(R.layout.notification_fragment, container, false);
        recyclerView = root.findViewById(R.id.shared_notification_recyclerview);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationAdapter = new NotificationAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(notificationAdapter);
        if (getArguments() != null) {
            NotificationFragmentArgs fragmentArgs = NotificationFragmentArgs.fromBundle(getArguments());
            userRole = fragmentArgs.getToRole();
            userId = String.valueOf(fragmentArgs.getToId());
        }
        mViewModel.getNotificationList().observe(getViewLifecycleOwner(), new Observer<List<NotificationItem>>() {
            @Override
            public void onChanged(List<NotificationItem> notificationItems) {
                notificationAdapter.notifyDataSetChanged();
            }
        });
        getNotificationsList();
    }

    private void updateNotificationSeen(int id) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.SET_NOTIFICATION_SEEN,
                null, null
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(requireContext());
                String token = mySharedPreference.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("notification_id", String.valueOf(id));
                return params;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    private void getNotificationsList() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AppConstants.GET_NOTIFICATION_LIST + "?to_role=" + userRole + "&to_id=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = handleApiResponse(response);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("notifications_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                NotificationItem notificationItem = new NotificationItem(
                                        jsonObject1.getInt("id"),
                                        jsonObject1.getString("notification_text"),
                                        jsonObject1.getBoolean("is_seen"),
                                        jsonObject1.getString("notification_time")
                                );
                                mViewModel.addNotification(notificationItem);
                            }
                            notificationAdapter.notifyDataSetChanged();
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
                MySharedPreference mySharedPreference = new MySharedPreference(requireContext());
                String token = mySharedPreference.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    private class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, timeTextView;
        private View seenIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.notification_list_item_textview);
            timeTextView = itemView.findViewById(R.id.notification_list_item_time);
            seenIndicator = itemView.findViewById(R.id.notification_list_seen_indicator);
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NotificationViewHolder(
                    View.inflate(getContext(), R.layout.notification_item_layout, null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            NotificationItem notificationItem = mViewModel.getNotificationList().getValue().get(position);
            holder.textView.setText(notificationItem.getNotificationText());
            holder.seenIndicator.setVisibility(notificationItem.isSeen() ? View.GONE : View.VISIBLE);
            holder.timeTextView.setText(notificationItem.getNotificationTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!notificationItem.isSeen()) {
                        notificationItem.setSeen(true);
                        notificationAdapter.notifyDataSetChanged();
                        updateNotificationSeen(notificationItem.getId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mViewModel.getNotificationList().getValue().size();
        }
    }
}