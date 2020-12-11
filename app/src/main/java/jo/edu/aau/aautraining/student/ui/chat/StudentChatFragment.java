package jo.edu.aau.aautraining.student.ui.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jo.edu.aau.aautraining.R;

public class StudentChatFragment extends Fragment {

    private StudentChatViewModel mViewModel;

    public static StudentChatFragment newInstance() {
        return new StudentChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(StudentChatViewModel.class);
        View root =  inflater.inflate(R.layout.student_chat_fragment, container, false);
//        final TextView textView = root.findViewById(R.id.student_chat_contactName);
//        mViewModel.getContactName().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StudentChatViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.student_chat_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(new ChatAdapter());
        if(getArguments()!=null){
            String contactName = StudentChatFragmentArgs.fromBundle(getArguments()).getContactName();
            mViewModel.setContactName(contactName);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chat | " + contactName);
        }
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chat_item_textview);
        }
    }
    private class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==0)
                return new ChatViewHolder(
                        View.inflate(getContext(),R.layout.chat_item_layout_from,null)
                );

            return new ChatViewHolder(
                    View.inflate(getContext(),R.layout.chat_item_layout_to,null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            if(getItemViewType(position)==0){
                holder.textView.setText("This message from ......");
            }else{
                holder.textView.setText("This message to ......");
            }
        }

        @Override
        public int getItemViewType(int position) {
            //return super.getItemViewType(position);
            return position%2;
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}