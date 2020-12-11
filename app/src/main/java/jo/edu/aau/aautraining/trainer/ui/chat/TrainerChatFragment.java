package jo.edu.aau.aautraining.trainer.ui.chat;

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
import jo.edu.aau.aautraining.student.ui.chat.StudentChatFragment;
import jo.edu.aau.aautraining.student.ui.chat.StudentChatFragmentArgs;

public class TrainerChatFragment extends Fragment {

    private TrainerChatViewModel mViewModel;

    public static TrainerChatFragment newInstance() {
        return new TrainerChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(TrainerChatViewModel.class);
        return inflater.inflate(R.layout.trainer_chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.trainer_chat_recyclerview);
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