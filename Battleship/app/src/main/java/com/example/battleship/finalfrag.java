package com.example.battleship;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class finalfrag extends DialogFragment {
    private TextView mEditText;
    private Button goHome;
    private Button playAgain;

    public finalfrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static finalfrag newInstance(String title) {
        finalfrag frag = new finalfrag();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.finalresult, container);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = view.findViewById(R.id.finalmessage);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        mEditText.setText(title);
        if(title.equals("Defeat!")){
            mEditText.setTextColor(Color.RED);
        }
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        goHome = view.findViewById(R.id.gohome);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goDown = new Intent(getActivity(), MainMenu.class);
                startActivity(goDown);
            }
        });
        playAgain = view.findViewById(R.id.playagain);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goDown = new Intent(getActivity(), MainActivity.class);
                startActivity(goDown);
            }
        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
