package jo.edu.aau.aautraining.shared;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatFirebaseDB {
    private DatabaseReference mDatabaseFrom, mDatabaseTo;
    private Context context;
    private String toRole;
    private String toId;
    private String dbGuid;

    public ChatFirebaseDB(String fromRole, int fromId, String toRole, int toId, String dbGuid, Context context) {
        FirebaseApp.initializeApp(context);
        this.toId = String.valueOf(toId);
        this.toRole = toRole;
        this.dbGuid = dbGuid;
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://aau-training-default-rtdb.firebaseio.com/");
        mDatabaseFrom = database.getReference("messages").child(fromRole).child(String.valueOf(fromId)).child(dbGuid);
        mDatabaseTo = database.getReference("messages").child(toRole).child(String.valueOf(toId)).child(dbGuid);
        this.context = context;
    }

    public void addMessage(MessageModel messageModel) {
        messageModel.setTime(String.valueOf(Calendar.getInstance().getTime().getTime()));
        String key = mDatabaseFrom.push().getKey();
        messageModel.setFromOrTo("From");
        mDatabaseFrom.child(key).setValue(messageModel);
        mDatabaseFrom.child(key).child("time").setValue(ServerValue.TIMESTAMP);
        key = mDatabaseTo.push().getKey();
        messageModel.setFromOrTo("To");
        mDatabaseTo.child(key).setValue(messageModel);
        mDatabaseTo.child(key).child("time").setValue(ServerValue.TIMESTAMP);
        updateHasNewMessage();
    }

    private void updateHasNewMessage() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.UPDATE_HAS_NEW_MESSAGE,
                null, null
        ) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("to_role", toRole);
                params.put("to_id", toId);
                params.put("db_guid", dbGuid);
                params.put("has_new", "1");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                MySharedPreference mySharedPreference = new MySharedPreference(context);
                String token = mySharedPreference.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void addChildEventListener(ChildEventListener childEventListener) {
        mDatabaseFrom.addChildEventListener(childEventListener);
    }
}
