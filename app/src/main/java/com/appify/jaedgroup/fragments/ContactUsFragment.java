package com.appify.jaedgroup.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appify.jaedgroup.R;
import com.appify.jaedgroup.model.Message;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactUsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactUsFragment extends Fragment {
    private EditText nameEt;
    private EditText emailEt;
    private EditText msgEt;
    private Button sendBtn;
    private View layout;

    private Handler mHandler;

    private OnFragmentInteractionListener mListener;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        nameEt = view.findViewById(R.id.name_et);
        emailEt = view.findViewById(R.id.email_et);
        msgEt = view.findViewById(R.id.message_et);
        sendBtn = view.findViewById(R.id.send_btn);
        layout = getActivity().findViewById(R.id.layout);

        mHandler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (nameEt.getText().toString().length() < 1 || emailEt.getText().toString().length() < 1
                || msgEt.getText().toString().length() < 1) {
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setEnabled(true);
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(runnable);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tasks.checkNetworkStatus(getContext())) {
                    sendMessage();
                } else {
                    tasks.makeSnackbar(layout,"Phone not connect to Internet. Pls connect and try again");
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnTransactionClickListener) {
//            mListener = (OnTransactionClickListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnTransactionClickListener");
//        }
  }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void sendMessage() {
        String id = FirebaseDatabase.getInstance().getReference().push().getKey();
        String name = nameEt.getText().toString();
        String email = nameEt.getText().toString();
        String message = msgEt.getText().toString();
        String userId = FirebaseAuth.getInstance().getUid();

        Message msg = new Message(id, name, email, message, userId);

        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        messageRef.child(id).setValue(msg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Message successfully sent", Toast.LENGTH_LONG).show();
                            clearFields();
                        } else {
                            Toast.makeText(getContext(), "Message not successfully sent", Toast.LENGTH_LONG).show();
                            Log.e("msg", task.getException().toString());
                        }
                    }
                });
    }

    private void clearFields() {
        nameEt.setText("");
        msgEt.setText("");
        emailEt.setText("");
    }
}
