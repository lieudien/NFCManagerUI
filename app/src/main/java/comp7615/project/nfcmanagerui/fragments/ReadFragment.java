package comp7615.project.nfcmanagerui.fragments;

import android.content.Context;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

import comp7615.project.nfcmanagerui.R;
import comp7615.project.nfcmanagerui.activities.MainActivity;
import comp7615.project.nfcmanagerui.adapters.ReadingAdapter;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ReadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {

    public static final String TAG = ReadFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private Map<String, String> mData;
    private NfcAdapter nfcAdapter;
    private Context context;
    private TextView tvMessage;

    public ReadFragment() {};

    public static ReadFragment newInstance() {
        return new ReadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initNFC();

        mData = new LinkedHashMap<String, String>();
        mData.put("Tag Type", "NFCTag");
        mData.put("Technologies available", "7D");
        mData.put("Serial number", "Final Project");
        mData.put("ATQA", "Nowhere");
        mData.put("SAK", "0x00");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        initViews(view);

        return view;
    }

    protected void initViews(View v) {
        ListView listView = v.findViewById(R.id.lvData);
        ReadingAdapter adapter = new ReadingAdapter(mData);
        listView.setAdapter(adapter);

        tvMessage = v.findViewById(R.id.tvMessage);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(getActivity(), "Read Fragment Added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    protected void initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);

        if (nfcAdapter == null) {
            Toast.makeText(context, "This device doesn't support NFC Tag", Toast.LENGTH_SHORT).show();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(context, "NFC is disabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "NFC is on", Toast.LENGTH_SHORT).show();
        }
    }

    public void onNfcDetected(Ndef ndef) {
        readFromNFC(ndef);
    }

    private void readFromNFC(Ndef ndef) {
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            Log.d("DEBUG", "readFromNFC: " + message);
            tvMessage.setText("NdefMessage: " + message);
            ndef.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
