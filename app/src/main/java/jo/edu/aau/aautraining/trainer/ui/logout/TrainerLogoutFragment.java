package jo.edu.aau.aautraining.trainer.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class TrainerLogoutFragment extends Fragment {


    public TrainerLogoutFragment() {
        // Required empty public constructor
    }
    public static TrainerLogoutFragment newInstance(String param1, String param2) {
        TrainerLogoutFragment fragment = new TrainerLogoutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.trainer_logout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.trainer_logout_cancelBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(getView()).navigateUp();
                    }
                }
        );
        view.findViewById(R.id.trainer_logout_confirmBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MySharedPreference mySharedPreference=new MySharedPreference(getContext());
                        mySharedPreference.setToken("");
                        Intent i = new Intent(getContext(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                }
        );
    }
}