package comp7615.project.nfcmanagerui.fragments;


import android.app.DialogFragment;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

        nfcDialogListener = (NfcWriteActivity)context;
        nfcDialogListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        nfcDialogListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef, NdefMessage messageToWrite) {

        prgbWriteStatus.setVisibility(View.VISIBLE);
        writeToNfc(ndef, messageToWrite);
    }

    private void writeToNfc(Ndef ndef, NdefMessage message) {

        txtvMessage.setText(getString(R.string.msg_write_progress));

        if (ndef != null) {

            try {
                ndef.connect();
                // todo: swap to message param once tested
                NdefRecord uriRecord = NdefRecord.createUri("geo: 49.2578263, -123.193944");
                ndef.writeNdefMessage(new NdefMessage(uriRecord));
                ndef.close();

                //Write Successful
                txtvMessage.setText(getString(R.string.msg_write_success));
            }
            catch (IOException | FormatException e) {
                e.printStackTrace();
                txtvMessage.setText(getString(R.string.msg_write_error));
            }
            finally {
                prgbWriteStatus.setVisibility(View.GONE);
            }
        }
    }
}
