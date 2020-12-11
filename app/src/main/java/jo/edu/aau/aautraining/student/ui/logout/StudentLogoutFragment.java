package jo.edu.aau.aautraining.student.ui.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.MySharedPreference;
import jo.edu.aau.aautraining.student.StudentMainActivity;

public class StudentLogoutFragment extends Fragment {


    public StudentLogoutFragment() {
        // Required empty public constructor
    }
    public static StudentLogoutFragment newInstance(String param1, String param2) {
        StudentLogoutFragment fragment = new StudentLogoutFragment();
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
        return inflater.inflate(R.layout.fragment_studnet_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.student_logout_cancelBtn).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_student_nav_logout_to_student_nav_profile)
        );
        view.findViewById(R.id.student_logout_confirmBtn).setOnClickListener(
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