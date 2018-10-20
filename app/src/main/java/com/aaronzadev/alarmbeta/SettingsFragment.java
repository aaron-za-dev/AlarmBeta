package com.aaronzadev.alarmbeta;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

/**
 * Created by Aaron Zuniga on 08/06/2016.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context c;
    private Preference preference;
    private ListPreference listContactOne, listContactTwo, listContactThree;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        c = getActivity().getApplicationContext();
        fillPreferencesContacts();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case "pref_contactOne":
                preference = findPreference(key);
                preference.setSummary(sharedPreferences.getString(key, ""));
                break;
            case "pref_contactTwo":
                preference = findPreference(key);
                preference.setSummary(sharedPreferences.getString(key, ""));
                break;
            case "pref_contactThree":
                preference = findPreference(key);
                preference.setSummary(sharedPreferences.getString(key, ""));
                break;
            case "pref_num_alarms":
                preference = findPreference(key);
                switch (sharedPreferences.getString(key, "")) {
                    case "1":
                        preference.setSummary(getResources().getString(R.string.oneAlarm));
                        break;
                    case "2":
                        preference.setSummary(getResources().getString(R.string.twoAlarm));
                        break;
                    case "3":
                        preference.setSummary(getResources().getString(R.string.threeAlarm));
                        break;
                    case "4":
                        preference.setSummary(getResources().getString(R.string.fourAlarm));
                        break;
                    case "5":
                        preference.setSummary(getResources().getString(R.string.fiveAlarm));
                        break;
                }
                break;
            case "pref_time_alarms":
                preference = findPreference(key);
                switch (sharedPreferences.getString(key, "")) {
                    case "30000":
                        preference.setSummary(getResources().getString(R.string.midMinute));
                        break;
                    case "60000":
                        preference.setSummary(getResources().getString(R.string.oneMinute));
                        break;
                    case "180000":
                        preference.setSummary(getResources().getString(R.string.threeMinute));
                        break;
                    case "300000":
                        preference.setSummary(getResources().getString(R.string.fiveMinute));
                        break;
                }
                break;
            case "alarm_msj":
                preference = findPreference(key);
                preference.setSummary(sharedPreferences.getString(key, ""));
                break;


        }


    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    private void fillPreferencesContacts() {


        listContactOne = (ListPreference) findPreference("pref_contactOne");
        listContactTwo = (ListPreference) findPreference("pref_contactTwo");
        listContactThree = (ListPreference) findPreference("pref_contactThree");

        if (listContactOne != null && listContactTwo != null && listContactThree != null) {

            ArrayList<Contact> contacts = getContacts();

            if (contacts.size() > 0) {

                CharSequence entries[] = new String[contacts.size()];
                CharSequence entryValues[] = new String[contacts.size()];

                int i = 0;
                for (Contact contact : contacts) {
                    entries[i] = contact.getContactName();
                    entryValues[i] = contact.getContactNumber();
                    i++;
                }

                listContactOne.setEntries(entries);
                listContactOne.setEntryValues(entryValues);
                listContactTwo.setEntries(entries);
                listContactTwo.setEntryValues(entryValues);
                listContactThree.setEntries(entries);
                listContactThree.setEntryValues(entryValues);

            } else {

                CharSequence entries[] = {"No hay Contactos Disponibles"};
                CharSequence entryValues[] = {"0"};

                listContactOne.setEntries(entries);
                listContactOne.setEntryValues(entryValues);
                listContactTwo.setEntries(entries);
                listContactTwo.setEntryValues(entryValues);
                listContactThree.setEntries(entries);
                listContactThree.setEntryValues(entryValues);

            }


        }


    }


    public ArrayList<Contact> getContacts() {

        ArrayList<Contact> allContacts = new ArrayList<Contact>();
        ContentResolver cr = c.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1", null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        allContacts.add(new Contact(contactName, contactNumber));
                        //break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());

        }

        return allContacts;
    }


}