package comp7615.project.nfcmanagerui.fragments;


import android.app.DialogFragment;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import comp7615.project.nfcmanagerui.R;
import comp7615.project.nfcmanagerui.activities.NfcWriteActivity;
import comp7615.project.nfcmanagerui.listeners.DialogListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NfcWriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NfcWriteFragment extends DialogFragment
{

    public static final String TAG = NfcWriteFragment.class.getSimpleName();

    public static NfcWriteFragment newInstance() {
        return new NfcWriteFragment();
    }

    private TextView txtvMessage;
    private ProgressBar prgbWriteStatus;
    private DialogListener nfcDialogListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nfc_write, container,false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        txtvMessage     = (TextView) view.findViewById(R.id.tv_message);
        prgbWriteStatus = (ProgressBar) view.findViewById(R.id.progress);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DEBUG", "OnAttach: running");
        nfcDialogListener = (NfcWriteActivity)context;
        nfcDialogListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DEBUG", "OnDetach: running");
        nfcDialogListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef, Tag tag, NdefMessage messageToWrite) {

        prgbWriteStatus.setVisibility(View.VISIBLE);
        writeToNfc(ndef, tag, messageToWrite);
    }

    private void writeToNfc(Ndef ndef, Tag tag, NdefMessage message) {

        txtvMessage.setText(getString(R.string.msg_write_progress));
        Log.d("DEBUG", "writeToNfc: before check ndef");

        if (ndef != null) {

            try {
                ndef.connect();
                Log.d("DEBUG", "Connect to the tag");
                // todo: swap to message param once tested
                NdefRecord uriRecord = NdefRecord.createUri("https://www.google.com/maps/dir/Current+Location/49.2578263,-123.193944");
                ndef.writeNdefMessage(new NdefMessage(uriRecord));
                Log.d("DEBUG", "Finish writing to tag");

                //Write Successful
                txtvMessage.setText(getString(R.string.msg_write_success));
            }
            catch (Exception e) {
                Log.d("DEBUG", String.format("Raise exception: %s", e.getMessage()));
                e.printStackTrace();
                txtvMessage.setText(getString(R.string.msg_write_error));
            }
            finally {
                prgbWriteStatus.setVisibility(View.GONE);
                try {
                    Log.d("DEBUG", "ndef close");
                    ndef.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } else {
            Log.d("DEBUG", "ndef is null");//
//            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
//            if (ndefFormatable != null) {
//                Log.d("DEBUG", "ndefFormatable is not null");
//                try {
//                    ndefFormatable.connect();
//                    Log.d("DEBUG", "Connect to the tag");
//                    ndefFormatable.format(message);
//                    Log.d("DEBUG", "Format message");
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    try {
//                        Log.d("DEBUG", "ndef close");
//                        ndefFormatable.close();
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else {
//                Log.d("DEBUG", "ndefFormatable is null");
//            }

        }
    }
}
