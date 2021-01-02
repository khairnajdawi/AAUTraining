package jo.edu.aau.aautraining.shared;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;


public class MyAppCompatActivity extends AppCompatActivity {

    private final String RequestQueueTag = "AAU";
    protected MySharedPreference mySharedPreference;
    private Snackbar snackbar;
    private View progressView;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySharedPreference = new MySharedPreference(this);
        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        });
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void showProgressView() {
        progressView.setVisibility(View.VISIBLE);
    }

    public void hideProgressView() {
        progressView.setVisibility(View.GONE);
    }

    public void addVolleyStringRequest(StringRequest stringRequest) {
        stringRequest.setTag(RequestQueueTag);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(RequestQueueTag);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        progressView = findViewById(R.id.progressView);
        View parentLayout = findViewById(android.R.id.content);
        snackbar = Snackbar.make(parentLayout, getResources().getString(R.string.error), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.navbar_size);
        snackbar.getView().setLayoutParams(layoutParams);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        setSnackbarActionDismiss();
    }

    protected void showSnackbar() {
        snackbar.show();
    }

    public void showSnackbar(int msgResId) {
        snackbar.setText(msgResId);
        snackbar.show();
    }

    protected void setSnackbarActionDismiss() {
        snackbar.setAction(R.string.dismiss, view -> snackbar.dismiss());
    }

    public String handleApiResponse(String responseString) {
        Log.d("Api Response", responseString);
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            if (!jsonObject.getBoolean("valid")) {
                showSnackbar();
                hideProgressView();
                return "";
            }
            if (!jsonObject.getBoolean("auth_status")) {
                signOutAuthFail();
                return "";
            }
            return jsonObject.getString("response");
        } catch (Exception exc) {
            exc.printStackTrace();
            showSnackbar();
            hideProgressView();
        }
        return "";
    }

    protected void signOutAuthFail() {
        mySharedPreference.setToken("");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
