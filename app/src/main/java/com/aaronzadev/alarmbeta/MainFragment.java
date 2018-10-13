package com.aaronzadev.alarmbeta;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private RippleBackground background;
    private ImageView imgBtn;

    private boolean isClicked = false;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        background = view.findViewById(R.id.background);
        imgBtn = view.findViewById(R.id.btImg);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isClicked) {
                    //readPreferences();
                    //startService(intent);
                    //MostrarrPantalla();
                    isClicked = true;
                    background.startRippleAnimation();
                    //fab.setImageDrawable(getResources().getDrawable(R.drawable.alert));
                    Toast.makeText(getContext(), "Iniciando Alarma...", Toast.LENGTH_SHORT).show();
                } else {
                    //OcultarPantalla();
                    //stopService(intent);
                    isClicked = false;
                    background.stopRippleAnimation();
                    //fab.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    Toast.makeText(getContext(), "Deteniendo...", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
