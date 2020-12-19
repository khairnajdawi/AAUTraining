package jo.edu.aau.aautraining.shared.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jo.edu.aau.aautraining.R;

public class ChatFragment extends Fragment {

    private ChatViewModel mViewModel;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(ChatViewModel.class);
        View root = inflater.inflate(R.layout.shared_chat_fragment, container, false);
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
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.shared_chat_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(new ChatAdapter());
        if (getArguments() != null) {
            String contactName = ChatFragmentArgs.fromBundle(getArguments()).getContactName();
            mViewModel.setContactName(contactName);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat | " + contactName);
        }
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chat_item_textview);
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new ChatViewHolder(
                        View.inflate(getContext(), R.layout.chat_item_layout_from, null)
                );

            return new ChatViewHolder(
                    View.inflate(getContext(), R.layout.chat_item_layout_to, null)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                holder.textView.setText("This message from ......");
            } else {
                holder.textView.setText("This message to ......");
            }
        }

        @Override
        public int getItemViewType(int position) {
            //return super.getItemViewType(position);
            return position % 2;
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}