package jo.edu.aau.aautraining.shared.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class ChatListFragment extends MyFragment {

    private String fromRole;
    private int fromId;
    private ArrayList<ChatListItem> chatListItems;
    private ChatListAdapter chatListAdapter;

    public ChatListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatListItems = new ArrayList<>();
        chatListAdapter = new ChatListAdapter();
    }

    private void getChatList() {
        chatListItems = new ArrayList<>();
        showProgressView();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.GET_CHAT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            String response = handleApiResponse(s);
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ChatListItem chatListItem = new ChatListItem();
                                chatListItem.setContactName(jsonObject.getString("contact_name"));
                                chatListItem.setDbGuid(jsonObject.getString("db_guid"));
                                chatListItem.setHasNewMessages(jsonObject.getBoolean("has_new_msg"));
                                chatListItem.setToId(jsonObject.getInt("to_id"));
                                chatListItem.setToRole(jsonObject.getString("to_role"));
                                chatListItems.add(chatListItem);
                            }
                            chatListAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showSnackbar(R.string.error);
                        }

                        hideProgressView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                showSnackbar(R.string.error);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("from_role", fromRole);
                params.put("from_id", String.valueOf(fromId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(getContext());
                headers.put("Authorization", "Bearer " + mySharedPreference.getToken());
                return headers;
            }
        };
        addVolleyStringRequest(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_list, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.chat_list_recyclerview);
        recyclerView.setAdapter(chatListAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            ChatListFragmentArgs chatListFragmentArgs = ChatListFragmentArgs.fromBundle(getArguments());
            fromId = chatListFragmentArgs.getFromId();
            fromRole = chatListFragmentArgs.getFromRole();
            getChatList();
        }
    }

    private class ChatListItem {
        private String contactName, toRole, dbGuid;
        private int toId;
        private boolean hasNewMessages;

        public ChatListItem() {
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getToRole() {
            return toRole;
        }

        public void setToRole(String toRole) {
            this.toRole = toRole;
        }

        public String getDbGuid() {
            return dbGuid;
        }

        public void setDbGuid(String dbGuid) {
            this.dbGuid = dbGuid;
        }

        public int getToId() {
            return toId;
        }

        public void setToId(int toId) {
            this.toId = toId;
        }

        public boolean isHasNewMessages() {
            return hasNewMessages;
        }

        public void setHasNewMessages(boolean hasNewMessages) {
            this.hasNewMessages = hasNewMessages;
        }
    }

    private class ChatListViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        View newIndicatorView;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chat_list_item_textview);
            newIndicatorView = itemView.findViewById(R.id.chat_list_new_indicator);
        }
    }

    private class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {

        @NonNull
        @Override
        public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatListViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item_layout, null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
            ChatListItem chatListItem = chatListItems.get(position);
            holder.textView.setText(
                    String.format("%s : (%s)", chatListItem.getContactName(), chatListItem.getToRole())
            );
            Bundle bundle = new Bundle();
            bundle.putString("contact_name", chatListItem.getContactName());
            bundle.putString("db_guid", chatListItem.getDbGuid());
            bundle.putString("to_role", chatListItem.getToRole());
            bundle.putInt("to_id", chatListItem.getToId());
            bundle.putString("from_role", fromRole);
            bundle.putInt("from_id", fromId);

            if (fromRole.equalsIgnoreCase("trainer")) {
                holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_trainer_nav_chat_list_to_trainer_nav_chat, bundle));
            } else if (fromRole.equalsIgnoreCase("student")) {
                holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_student_nav_chat_list_to_student_nav_chat, bundle));
            } else if (fromRole.equalsIgnoreCase("supervisor")) {
                holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_supervisor_nav_chat_list_to_supervisor_nav_chat, bundle));
            }
            if (chatListItem.isHasNewMessages()) {
                holder.newIndicatorView.setVisibility(View.VISIBLE);
            } else {
                holder.newIndicatorView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return chatListItems.size();
        }
    }
}