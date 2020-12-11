package jo.edu.aau.aautraining.student.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jo.edu.aau.aautraining.R;

public class StudentBlankFragment extends Fragment {

    public StudentBlankFragment() {
        // Required empty public constructor
    }

    public static StudentBlankFragment newInstance() {
        StudentBlankFragment fragment = new StudentBlankFragment();
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
        return inflater.inflate(R.layout.fragment_student_blank, container, false);
    }
}