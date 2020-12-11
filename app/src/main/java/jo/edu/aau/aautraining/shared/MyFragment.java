package jo.edu.aau.aautraining.shared;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;

public class MyFragment extends Fragment {

    private View progressView;
    public RequestQueue mRequestQueue;
    public ImageLoader mImageLoader;
    protected MySharedPreference mySharedPreference;
    private Snackbar snackbar;
    private final String RequestQueueTag = "AAU";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressView=view.findViewById(R.id.progressView);
        snackbar = Snackbar.make(getActivity().getWindow().getDecorView(), getResources().getString(R.string.error), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.navbar_size);
        snackbar.getView().setLayoutParams(layoutParams);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        setSnackbarActionDismiss();
    }
    protected void showProgressView(){
        progressView.setVisibility(View.VISIBLE);
    }
    protected void hideProgressView(){
        progressView.setVisibility(View.GONE);
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

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRequestQueue.cancelAll(RequestQueueTag);
    }
    public void addVolleyStringRequest(StringRequest stringRequest) {
        stringRequest.setTag(RequestQueueTag);
        mRequestQueue.add(stringRequest);
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
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
