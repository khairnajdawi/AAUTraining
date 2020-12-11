package jo.edu.aau.aautraining.shared;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;

/**
 * Created by KhairAllah on 28/09/2018.
 */

public class MyAppCompatActivity extends AppCompatActivity {

    private final String RequestQueueTag = "AAU";
    protected MySharedPreference mySharedPreference;
    private Snackbar snackbar;
    private List<AsyncTask> asyncTaskList;
    private View progressView;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        asyncTaskList = new ArrayList<>();
        mySharedPreference = new MySharedPreference(this);
        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        });
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void showProgressView() {
        progressView.setVisibility(View.VISIBLE);
    }

    public void hideProgressView() {
        progressView.setVisibility(View.GONE);
    }


    protected void addAsyncTask(AsyncTask asyncTask) {
        asyncTaskList.add(asyncTask);
    }

    public void addVolleyStringRequest(StringRequest stringRequest) {
        stringRequest.setTag(RequestQueueTag);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AsyncTask asyncTask : asyncTaskList) {
            if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                asyncTask.cancel(true);
            }
        }
        requestQueue.cancelAll(RequestQueueTag);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        progressView = findViewById(R.id.progressView);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(getResources().getDrawable(R.mipmap.actionbar_logo));
        }*/
        snackbar = Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.navbar_size);
        snackbar.getView().setLayoutParams(layoutParams);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        setSnackbarActionDismiss();
    }

    private void setSnackbarDurationIndefinite() {
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
    }

    private void setSnackbarDurationLong() {
        snackbar.setDuration(Snackbar.LENGTH_LONG);
    }

    private void setSnackbarDurationShort() {
        snackbar.setDuration(Snackbar.LENGTH_SHORT);
    }

    protected void hideSnackbar() {
        snackbar.dismiss();
    }

    protected void setSnackbarMessage(int msgResId) {
        snackbar.setText(msgResId);
    }

    protected void showSnackbar() {
        snackbar.show();
    }

    public void showSnackbar(int msgResId) {
        snackbar.setText(msgResId);
        snackbar.show();
    }

    protected void setSnackbarActionDismiss() {
        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
    }

    protected void setSnackbarAction(int stringResId, View.OnClickListener ocl) {
        snackbar.setAction(stringResId, ocl);
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
            if (!jsonObject.getBoolean("auth_status") ) {
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
