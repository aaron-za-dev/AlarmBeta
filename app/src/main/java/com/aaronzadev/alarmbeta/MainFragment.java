package com.aaronzadev.alarmbeta;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;
import com.vistrav.ask.Ask;

import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private RippleBackground background;
    private ImageView imgBtn;
    private Intent intent;
    private Preferences preferences;
    private int TotalContacts = 0;
    private View viewParent;
    private int numberAlarms;
    private long timeXAlarm;
    private String msjAlarm;

    private BottomNavigationView navView;

    private boolean isClicked = false;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navView = view.findViewById(R.id.navigationView);

        background = view.findViewById(R.id.background);
        imgBtn = view.findViewById(R.id.btImg);

        viewParent = imgBtn;

        intent = new Intent(getContext(), SendLocationService.class);

        preferences = new Preferences(getContext());

        verifyContacts();

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isClicked) {
                    readPreferences();
                    getActivity().startService(intent);
                    isClicked = true;
                    imgBtn.setImageDrawable(getResources().getDrawable(R.drawable.siren_enabled));
                    background.startRippleAnimation();
                    //fab.setImageDrawable(getResources().getDrawable(R.drawable.alert));
                    Toast.makeText(getContext(), "Iniciando Alarma...", Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().stopService(intent);
                    isClicked = false;
                    imgBtn.setImageDrawable(getResources().getDrawable(R.drawable.siren));
                    background.stopRippleAnimation();
                    Toast.makeText(getContext(), "Deteniendo...", Toast.LENGTH_SHORT).show();
                }


            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.about_menu:
                        Navigation.findNavController(view).navigate(R.id.aboutFragment);
                        return true;
                    case R.id.settings_menu:
                        Navigation.findNavController(view).navigate(R.id.fragSettings);
                        return true;
                    default:
                        return false;
                }

            }
        });

        chekPermissions();
    }

    private void chekPermissions() {

        Ask.on(this).forPermissions(Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS)
                .withRationales("Debes permitir el acceso a tus contactos",
                        "Debes Permitirnos ubicarte",
                        "Debes permitirnos enviar las alarmas via SMS")
                .go();

    }

    private void verifyContacts() {

        if (preferences.readContactOne("pref_contactOne").equals("") && preferences.readContactTwo("pref_contactTwo").equals("") && preferences.readContactThree("pref_contactThree").equals("")) {

            imgBtn.setEnabled(false);

            Snackbar.make(viewParent, "No Tienes Contactos Registrados", Snackbar.LENGTH_INDEFINITE).setAction("Registrar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(viewParent).navigate(R.id.fragSettings);

                }
            }).show();

        } else {
            if (!preferences.readContactOne("pref_contactOne").equals("")) {

                intent.putExtra("C1", preferences.readContactOne("pref_contactOne"));
                TotalContacts++;
                intent.putExtra("TotalContacts", TotalContacts);
            }
            if (!preferences.readContactTwo("pref_contactTwo").equals("")) {

                intent.putExtra("C2", preferences.readContactTwo("pref_contactTwo"));
                TotalContacts++;
                intent.putExtra("TotalContacts", TotalContacts);
            }
            if (!preferences.readContactThree("pref_contactThree").equals("")) {

                intent.putExtra("C3", preferences.readContactThree("pref_contactThree"));
                TotalContacts++;
                intent.putExtra("TotalContacts", TotalContacts);
            }
            imgBtn.setEnabled(true);
        }


    }

    private void readPreferences() {

        numberAlarms = Integer.parseInt(preferences.readNumberOfAlarms("pref_num_alarms"));
        intent.putExtra("QtySMS", numberAlarms);

        timeXAlarm = Long.parseLong(preferences.readTimeOfAlarms("pref_time_alarms"));
        intent.putExtra("TIMEXALARM", timeXAlarm);

        msjAlarm = preferences.readMsjOfAlarm("alarm_msj");
        intent.putExtra("MSJALARM", msjAlarm);

    }
}
