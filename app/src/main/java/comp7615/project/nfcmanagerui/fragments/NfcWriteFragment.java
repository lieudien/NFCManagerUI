package comp7615.project.nfcmanagerui.fragments;


import android.app.DialogFragment;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import comp7615.project.nfcmanagerui.R;
import comp7615.project.nfcmanagerui.activities.NfcWriteActivity;
import comp7615.project.nfcmanagerui.listeners.IDialogListener;

/**
 * Handles the actual writing of an NFC tag. If wanted attach an IDialogListener to the implementing
 * activity to handle what to do when they dialog popup is shown/hidden
 */
public class NfcWriteFragment extends DialogFragment
{

    public static final String TAG = NfcWriteFragment.class.getSimpleName();

    public static NfcWriteFragment newInstance() {
        return new NfcWriteFragment();
    }

    private TextView txtvMessage;
    private ProgressBar prgbWriteStatus;
    private IDialogListener nfcDialogListener;

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

    /**
     * Let listener activity know dialog has been shown.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DEBUG", "OnAttach: running");
        nfcDialogListener = (NfcWriteActivity)context;
        nfcDialogListener.onDialogDisplayed();
    }

    /**
     * Let listener activity know dialog has been dismissed.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("DEBUG", "OnDetach: running");
        nfcDialogListener.onDialogDismissed();
    }

    /**
     * Show spinner wheel showing work in progress.
     *
     * @param tag - tag to format
     * @param messageToWrite - message to format tag with
     */
    public void onNfcDetected(Ndef ndef, Tag tag, NdefMessage messageToWrite) {

        prgbWriteStatus.setVisibility(View.VISIBLE);
        writeToNfc(ndef, tag, messageToWrite);
    }

    /**
     * Attempts to write to a tag in the NDEF format, if unable to NDEFORMATABLE will be attempted.
     * To be used if a tag is not already NDEF tech.
     *
     * @param tag - tag to format
     * @param message - message to format tag with
     */
    private void writeToNfc(Ndef ndef, final Tag tag, NdefMessage message) {

        txtvMessage.setText(getString(R.string.msg_write_progress));
        Log.d("DEBUG", "writeToNfc: before check ndef");

        if (ndef != null) {

            try {
                ndef.connect();
                Log.d("DEBUG", "Connect to the tag");
                ndef.writeNdefMessage(message);
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
            Log.d("DEBUG", "ndef is null");
            formatTag(tag, message);
        }
    }

    /**
     * Formats a tag. To be used if a tag is not already NDEF tech.
     *
     * @param tag - tag to format
     * @param ndefMessage - message to format tag with
     */
    protected void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                txtvMessage.setText("Tag is not ndef formattable");
                Log.d("DEBUG", "Tag is not ndef formattable");
            }
            else {
                ndefFormatable.connect();
                ndefFormatable.format(ndefMessage);
                ndefFormatable.close();
                txtvMessage.setText("Tag formatted and written");
                Log.d("DEBUG", "Tag written");
            }
        } catch (Exception e) {
            Log.d("DEBUG", String.format("Exception: %s", e.getMessage()));
        }
        finally {
            prgbWriteStatus.setVisibility(View.GONE);
        }
    }
}
