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

/**
 * Activity to manage user input and location lookup to be written to an NFC tag
 */
public class MapsActivity extends NfcWriteActivity implements OnMapReadyCallback {

    /** accepted non google map lookup string */
    private final String CURRENT_LOCATION = "current location";

    /** Value to use as start locaton when current location is selected */
    private final String URL_CURRENT_LOCATION = "current+location";

    /** Google Map to display chosen locations */
    private GoogleMap mMap;

    /** text box for the user to choose their source location */
    private EditText etSrcLocation;

    /** text box for the user to choose their destination location */
    private EditText etDstLocation;

    /** location value to store as the starting google map location for a route */
    private String srcLocation;

    /** location value to store as the destination google map location for a route */
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

        // grab user input objects
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

    /**
     * Sets the startingw location choice
     *
     * @param view
     */
    public void onSourceLocationGoClick(View view) {
        String userSourceLocation = etSrcLocation.getText().toString();

        setSrcLocationChoice(userSourceLocation);
    }

    /**
     * Sets the destination location choice
     *
     * @param view
     */
    public void onDestLocationGoClick(View view) {
        String userDestLocation = etDstLocation.getText().toString();

        setDestLocationChoice(userDestLocation);
    }

    /**
     * Searches for the first address in the address list. If found it will return the location.
     *
     * @param addressList - list with address to obtain
     *
     * @return - lat/long in the format [double,double]
     */
    protected String search(List<Address> addressList) {
        Address address = (Address) addressList.get(0);

        double latitude = address.getLatitude();
        double longitude = address.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        //Toast.makeText(this, String.format("LatLng: %f %f", latitude, longitude) , Toast.LENGTH_LONG).show();

        String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());

        mMap.addMarker(new MarkerOptions().position(latLng).title(addressText));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return latitude + "," + longitude;
    }

    private void setDestLocationChoice(String destChoice) {
        destLocation = getLocation(destChoice);

        // set found destination
        if (validDestLocation() ) {
            String displayName = getLocationName(destLocation);
            etDstLocation.setText(displayName);
        }
    }

    private void setSrcLocationChoice(String srcChoice) {
        // in this case, the src is valid
        if (srcChoice.equalsIgnoreCase(CURRENT_LOCATION) ) {
            srcLocation = URL_CURRENT_LOCATION;
            etSrcLocation.setText(CURRENT_LOCATION);
        }
        else {
            srcLocation = getLocation(srcChoice);

            if (validSourceLocation() ) {
                String displayName = getLocationName(srcLocation);
                etSrcLocation.setText(displayName);
            }
        }
    }

    /**
     * Gets the location name for the specified coordinates. The coordinates must be in the format
     * "latitude,longitude"
     *
     * @param locationCoords coordinates in proper format to find name for
     *
     * @return name found for location or null on failure to find a location
     */
    private String getLocationName(String locationCoords) {
        // get latitude and longitude
        String locationName = null;
        Address addr;
        String coords[]     = locationCoords.split(",");
        double latitude     = Double.parseDouble(coords[0]);
        double longitude    = Double.parseDouble(coords[1]);

        // Get geo names for location coordinates
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            addr         = addressList.get(0);
            locationName = addr.getAddressLine(0);
        }
        catch (IOException ex) {
            Toast.makeText(MapsActivity.this, "Invalid location name", Toast.LENGTH_SHORT).show();
        }

        return locationName;
    }

    /**
     * Attempts to get the location in the format [double,double].
     *
     * @param userLocation name of the location to get coordinates for.
     *
     * @return the locations lat/long in the explained format or null if no location was found.
     */
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

    /**
     * Validates the source location is a valid latitude and longitude or equals "current location"
     *
     * @return True if valid, false otherwise
     */
    private boolean validSourceLocation() {
        boolean isValid = false;

        if (srcLocation != null && srcLocation.equalsIgnoreCase(URL_CURRENT_LOCATION) ) {
            isValid = true;
        }

        // Location is not current location and needs further validation
        if (!isValid) {
            isValid = validateLocation(srcLocation);
        }

        return isValid;
    }

    /**
     * Validates the destination location is a valid latitude and longitude.
     *
     * @return True if valid, false otherwise
     */
    private boolean validDestLocation() {
        return validateLocation(destLocation);
    }

    /**
     * Validates a location string in the format [double],[double].
     *
     * @param location - location to validate for proper format
     *
     * @return true if the location is in valid format. false otherwise.
     */
    private boolean validateLocation(String location) {
        boolean isValid = false;

        if (location != null && !location.isEmpty() ) {
            String coords[] = location.split(",");

            if (coords.length == 2) {
                try {
                    double latitude = Double.parseDouble(coords[0]);
                    double longitude = Double.parseDouble(coords[1]);

                    isValid = true;
                }
                catch (Exception ex) {
                    Toast.makeText(MapsActivity.this, "Invalid location entered.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return isValid;
    }
}

