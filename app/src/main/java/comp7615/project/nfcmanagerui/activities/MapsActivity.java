package comp7615.project.nfcmanagerui.activities;

import android.location.Address;
import android.location.Geocoder;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import comp7615.project.nfcmanagerui.R;

public class MapsActivity extends NfcWriteActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText etSrcLocation;
    private EditText etDstLocation;
    private String srcLocation;
    private String destLocation;

    @Override
    protected Button setWriteButton() {
        return (Button) findViewById(R.id.btnSubmit);
    }

    @Override
    protected NdefRecord getNdefRecordToWrite() {
        String googleRouteUrl = "https://www.google.com/maps/dir/"+srcLocation+"/"+destLocation;

        return NdefRecord.createUri(googleRouteUrl);
    }

    @Override
    protected boolean validateInput() {
        return validSourceLocation() && validDestLocation();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_maps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        etSrcLocation = (EditText) findViewById(R.id.etSrcLocation);
        etDstLocation = (EditText) findViewById(R.id.etDestLocation);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng vancouver = new LatLng(49.2578263, -123.193944);
        mMap.addMarker(new MarkerOptions().position(vancouver).title("Vancouver, BC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(vancouver).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    protected String search(List<Address> addressList) {
        Address address = (Address) addressList.get(0);

        double latitude = address.getLatitude();
        double longitude = address.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        Toast.makeText(this, String.format("LatLng: %f %f", latitude, longitude) , Toast.LENGTH_LONG).show();

        String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());

        mMap.addMarker(new MarkerOptions().position(latLng).title(addressText));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return latitude + "," + longitude;
    }

    public void onSourceLocationGoClick(View view) {
        String userSourceLocation = etSrcLocation.getText().toString();
        srcLocation               = getLocation(userSourceLocation);
    }

    public void onDestLocationGoClick(View view) {
        String userDestLocation = etDstLocation.getText().toString();
        destLocation            = getLocation(userDestLocation);
    }

    private String getLocation(String userLocation) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addressList = null;
        String result = null;

        try {
            addressList = geocoder.getFromLocationName(userLocation, 3);

            if (addressList != null && !addressList.isEmpty()) {
                result = search(addressList);
            }
        }
        catch (IOException exception) {
            Toast.makeText(MapsActivity.this, "Invalid location name", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private boolean validSourceLocation() {
        return srcLocation != null && srcLocation.isEmpty() == false;
    }

    private boolean validDestLocation() {
        return destLocation != null && destLocation.isEmpty() == false;
    }
}

