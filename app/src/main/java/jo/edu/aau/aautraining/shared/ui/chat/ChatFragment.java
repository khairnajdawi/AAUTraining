package jo.edu.aau.aautraining.shared.ui.chat;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.ChatFirebaseDB;
import jo.edu.aau.aautraining.shared.MessageModel;
import jo.edu.aau.aautraining.shared.MyFragment;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class ChatFragment extends MyFragment {

    private ChatViewModel mViewModel;
    private ChatFirebaseDB chatFirebaseDB;
    private ArrayList<MessageModel> messagesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private String fromRole;
    private String toRole;
    private int fromId;
    private int toId;
    private String dbGuid;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(ChatViewModel.class);
        View root = inflater.inflate(R.layout.shared_chat_fragment, container, false);
        EditText editText = root.findViewById(R.id.shared_chat_editText);
        root.findViewById(R.id.shared_chat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatFirebaseDB != null) {
                    MessageModel messageModel = new MessageModel(
                            editText.getText().toString(),
                            "",
                            "From"
                    );
                    chatFirebaseDB.addMessage(messageModel);
                    editText.setText("");
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.shared_chat_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        RecyclerViewMargin decoration = new RecyclerViewMargin(20, 1);
        recyclerView.addItemDecoration(decoration);
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
        if (getArguments() != null) {
            ChatFragmentArgs fragmentArgs = ChatFragmentArgs.fromBundle(getArguments());
            String contactName = fragmentArgs.getContactName();
            fromRole = fragmentArgs.getFromRole();
            toRole = fragmentArgs.getToRole();
            fromId = fragmentArgs.getFromId();
            toId = fragmentArgs.getToId();
            dbGuid = fragmentArgs.getDbGuid();
            if (dbGuid.isEmpty()) {
                getChatDbGuid(fromRole, fromId, toRole, toId);
            } else {
                setupFirebaseDB();
            }
            mViewModel.setContactName(contactName);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat | " + contactName);
        }
    }


    private void getChatDbGuid(String fromRole, int fromId, String toRole, int toId) {
        showProgressView();
        messagesList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.GET_CHAT_GUID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            String response = handleApiResponse(s);
                            if (!response.isEmpty()) {
                                JSONObject profileJsonObject = new JSONObject(response);
                                dbGuid = profileJsonObject.getString("db_guid");
                                setupFirebaseDB();
                            } else {
                                showSnackbar(R.string.error);
                            }
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
                params.put("to_role", toRole);
                params.put("from_id", String.valueOf(fromId));
                params.put("to_id", String.valueOf(toId));
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

    private void setupFirebaseDB() {
        final SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        chatFirebaseDB = new ChatFirebaseDB(fromRole, fromId, toRole, toId, dbGuid, getContext());
        chatFirebaseDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                long timestamp = Long.parseLong(map.get("time").toString());
                MessageModel messageModel = new MessageModel(
                        map.get("msg").toString(),
                        sfd.format(new Date(timestamp)),
                        map.get("fromOrTo").toString()
                );
                messagesList.add(messageModel);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messagesList.size() - 1);
                setMessagesRead();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setMessagesRead();
    }

    private void setMessagesRead() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.UPDATE_HAS_NEW_MESSAGE,
                null, null
        ) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("to_role", fromRole);
                params.put("to_id", String.valueOf(fromId));
                params.put("db_guid", dbGuid);
                params.put("has_new", "0");
                return params;
            }

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

    private class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, timeTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chat_item_textview);
            timeTextView = itemView.findViewById(R.id.chat_item_time_textview);
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ChatViewHolder(
                        View.inflate(getContext(), R.layout.chat_item_layout_from, null)
                );

            return new ChatViewHolder(
                    View.inflate(getContext(), R.layout.chat_item_layout_to, null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            MessageModel messageModel = messagesList.get(position);
            holder.textView.setText(messageModel.getMsg());
            holder.timeTextView.setText(messageModel.getTime());
        }

        @Override
        public int getItemViewType(int position) {
            MessageModel messageModel = messagesList.get(position);
            return messageModel.getFromOrTo().equalsIgnoreCase("from") ? 1 : 0;
        }

        @Override
        public int getItemCount() {
            return messagesList.size();
        }
    }

    public class RecyclerViewMargin extends RecyclerView.ItemDecoration {
        private final int columns;
        private int margin;

        /**
         * constructor
         *
         * @param margin  desirable margin size in px between the views in the recyclerView
         * @param columns number of columns of the RecyclerView
         */
        public RecyclerViewMargin(@IntRange(from = 0) int margin, @IntRange(from = 0) int columns) {
            this.margin = margin;
            this.columns = columns;

        }

        /**
         * Set different margins for the items inside the recyclerView: no top margin for the first row
         * and no left margin for the first column.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildLayoutPosition(view);
            //set right margin to all
            outRect.right = margin;
            //set bottom margin to all
            outRect.bottom = margin;
            //we only add top margin to the first row
            if (position < columns) {
                outRect.top = margin;
            }
            //add left margin only to the first column
            if (position % columns == 0) {
                outRect.left = margin;
            }

        }
    }
}