package comp7615.project.nfcmanagerui.activities;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import comp7615.project.nfcmanagerui.fragments.OtherFragment;
import comp7615.project.nfcmanagerui.R;
import comp7615.project.nfcmanagerui.fragments.ReadFragment;
import comp7615.project.nfcmanagerui.fragments.WriteOptionFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private NfcAdapter nfcAdapter;
    private boolean isReading = false;
    private boolean isReadFragmentShown = true;
    private Fragment currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_read:
                    isReadFragmentShown = true;
                    currentFragment = ReadFragment.newInstance();
                    displayFragment(currentFragment, ReadFragment.TAG);
                    return true;
                case R.id.navigation_write:
                    isReadFragmentShown = false;
                    currentFragment = WriteOptionFragment.newInstance();
                    displayFragment(currentFragment, WriteOptionFragment.TAG);
                    return true;
                case R.id.navigation_other:
                    isReadFragmentShown = false;
                    currentFragment = OtherFragment.newInstance();
                    displayFragment(currentFragment, OtherFragment.TAG);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initNFC();
    }

    protected void initViews() {
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        displayFragment(ReadFragment.newInstance(), ReadFragment.TAG);
    }

    protected void initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC Tag", Toast.LENGTH_SHORT).show();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NFC is on", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter[] intentFilter = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        };
        String[][] techList = new String[][] {
                {Ndef.class.getName()},
                {NdefFormatable.class.getName()}
        };
        Intent intent = new Intent(getApplicationContext(), getClass()).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, techList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Ndef ndef = Ndef.get(tag);
            if (isReadFragmentShown && !isReading) {
                isReading = true;
                try {
                    ReadFragment readFragment = (ReadFragment) getFragmentManager().findFragmentByTag(ReadFragment.TAG);
                    readFragment.onNfcDetected(ndef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    protected void displayFragment(Fragment currentFragment, String tagName) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, currentFragment, tagName).commit();
    }


}
