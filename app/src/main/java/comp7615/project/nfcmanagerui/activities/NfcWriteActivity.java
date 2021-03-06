package comp7615.project.nfcmanagerui.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import comp7615.project.nfcmanagerui.R;
import comp7615.project.nfcmanagerui.fragments.NfcWriteFragment;
import comp7615.project.nfcmanagerui.listeners.IDialogListener;

/**
 * Created by r_pur on 11/15/2017.
 */


/**
 * Manages the fragment to write to an NFC tag. Child classes must provide logic to:
 *  - the id for the button to bring up the write to nfc popup
 *  - the record to be written to the NFC tag
 *  - validation for user input
 *  - the content view of the child class to be set (note this is needed in order to get the
 *    button object that will bring up the NFC tag writing popup
 */
public abstract class NfcWriteActivity extends FragmentActivity implements IDialogListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    /** NFC adapter */
    private NfcAdapter nfcAdapter;

    /** Button to open the NFC write fragment */
    private Button bttnWrite;

    /** Whether the NFC write dialog is being displayed or not */
    private NfcWriteFragment nfcWriteFrag;

    /** Whether the NFC write dialog is being displayed or not */
    private boolean isDialogDisplayed = false;

    /** Whether the Activity should accept and handle an tapped NFC tag */
    private boolean isWrite = false;

    /**
     * Sets the button to open the dialog.
     *
     * @return Button to be used to open the NFC write dialog
     */
    // try find view by id as well
    protected abstract Button setWriteButton();

    /**
     * Gets the message to be written to the NFC tag.
     *
     * @return message to write to NFC tag
     */
    protected abstract NdefRecord getNdefRecordToWrite();

    /**
     * Validates if the user input is valid. If valid, NFC write fragment will popup.
     *
     * @return True if user input is valid, false otherwise.
     */
    protected abstract boolean validateInput();

    /**
     * Gets the content view of the child activity.
     *
     * @return the content view to be set by the NfcWriteActivity class
     */
    protected abstract int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView() );

        initViews();
        initNfc();
    }

    private void initViews() {
        bttnWrite = setWriteButton();
        bttnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWriteFragment();
            }
        });
    }

    private void initNfc(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    /**
     * Upon valid input, shows the NFC tag tap popup.
     */
    private void showWriteFragment() {
        if (validateInput()) {
            isWrite = true;
            nfcWriteFrag = (NfcWriteFragment) getFragmentManager().findFragmentByTag(NfcWriteFragment.TAG);

            // couldn't find write frag using get fragment manager
            if (nfcWriteFrag == null) {
                nfcWriteFrag = NfcWriteFragment.newInstance();
            }

            nfcWriteFrag.show(getFragmentManager(), NfcWriteFragment.TAG);
        }
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite           = false;
    }

    /**
     * Handle how to accept an NFC tag when resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected       = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected      = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected      = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        String[][] techList = new String[][] {
                {Ndef.class.getName()},
                {NdefFormatable.class.getName()}
        };
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,0, new Intent(this, getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING), 0);

        if(nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, techList);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * Handle a tag being tapped. The tag will be handled only if the NFC dialog is displayed.
     * If the NFC dialog is being displayed, coordinate writing to the NFC tag.
     *
     * @param intent intent carrying the NFC tag tap.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: " + intent.getAction());
        if(tag != null) {
            //Toast.makeText(this, getString(R.string.msg_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed && isWrite) {
                isWrite = false;
                NdefMessage message = new NdefMessage(getNdefRecordToWrite());
                nfcWriteFrag = (NfcWriteFragment) getFragmentManager().findFragmentByTag(NfcWriteFragment.TAG);
                nfcWriteFrag.onNfcDetected(ndef, tag, message);
            }
            else {
                Toast.makeText(this, getString(R.string.msg_click_write), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
