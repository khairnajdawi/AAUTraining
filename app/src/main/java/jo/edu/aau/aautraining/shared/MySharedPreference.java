package jo.edu.aau.aautraining.shared;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {
    private SharedPreferences sharedPreferences;
    private Context context;
    private String prefsName = "AAUTraining";
    private String prefsUserName = "userName";
    private String prefsToken = "token";

    public MySharedPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public String getToken() {
        return sharedPreferences.getString(prefsToken, "");
    }

    public void setToken(String token) {
        getEditor().putString(prefsToken, token).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(prefsUserName, "");
    }

    public void setUserName(String userName) {
        getEditor().putString(prefsUserName, userName).apply();
    }

}
