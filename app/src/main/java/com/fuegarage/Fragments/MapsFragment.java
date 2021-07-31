package com.fuegarage.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.fuegarage.BookingActivity;
import com.fuegarage.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.fuegarage.MainActivity.database;
import static com.fuegarage.MainActivity.fueRef;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {


    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    private int R_Code = 111;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior ;
    Button sheetBookButton;
    RadioButton fue1RadioButton;
    TextView availableTextView;

    Long available = null;
    //////////////

    //google map object
    private GoogleMap mMap;

    //current and destination location objects
    Location myLocation=null;
    Location destinationLocation=null;
    protected LatLng start=null;

    protected LatLng end=null;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission=false;

    //polyline object
    private List<Polyline> polylines=null;

    ////
     LatLng myLocationLatLng=null;

    LatLng garage1 = new LatLng(30.0277391, 31.4930784);
    LatLng garage2 = new LatLng(30.0257951, 31.4925738);
    LatLng garage3 = new LatLng(30.0279836, 31.4897851);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        sheetBookButton = view.findViewById(R.id.sheet_book_bottom);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        client = LocationServices.getFusedLocationProviderClient(getContext());
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }else {
            getLocation();
            ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},R_Code );
        }


        ////////////////////////////////////
        LinearLayout linearLayout = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(150);
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setPeekHeight(150);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        /////////////////////////////
        View sheet = (ViewGroup) view.findViewById(R.id.bottom_sheet);
        sheet.findViewById(R.id.sheet_first_garage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                LatLng latLng = new LatLng(30.0277391, 31.4930784);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Future University Parking 1");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 14));
                mMap.addMarker(markerOptions).showInfoWindow();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheet.findViewById(R.id.sheet_second_garage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                LatLng latLng = new LatLng(30.0257951, 31.4925738);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Future University Parking 2");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 14));
                mMap.addMarker(markerOptions).showInfoWindow();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheet.findViewById(R.id.sheet_third_garage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                LatLng latLng = new LatLng(30.0279836, 31.4897851);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Future University Parking 3");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 14));
                mMap.addMarker(markerOptions).showInfoWindow();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        //////////////////////////
        sheet.findViewById(R.id.sheet_first_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Findroutes(myLocationLatLng,garage1);
               // Toast.makeText(getContext(), "111111111", Toast.LENGTH_SHORT).show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheet.findViewById(R.id.sheet_second_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Findroutes(myLocationLatLng,garage2);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheet.findViewById(R.id.sheet_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Findroutes(myLocationLatLng,garage3);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        //////////////////////////
        sheet.findViewById(R.id.sheet_first_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mapIntent= new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse("google.navigation:q=30.0277391,31.4930784"));
                mapIntent.setPackage("com.google.android.apps.maps");
//                mapIntent.setData(Uri.parse("geo:30.0277391, 31.4930784"));
                Intent  chooser = Intent.createChooser(mapIntent , "Launch Google Maps");
                startActivity(chooser);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheet.findViewById(R.id.sheet_second_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent= new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse("geo:30.0257951, 31.4925738"));
                Intent  chooser = Intent.createChooser(mapIntent , "Launch Google Maps");
                startActivity(chooser);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheet.findViewById(R.id.sheet_third_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent= new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse("geo:30.0279836, 31.4897851"));
                Intent  chooser = Intent.createChooser(mapIntent , "Launch Google Maps");
                startActivity(chooser);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        ///////////////////////////////
        availableTextView = view.findViewById(R.id.sheet_available_places);
        fue1RadioButton = view.findViewById(R.id.fue_radio_button);

        fueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long value = (Long) snapshot.getValue();
                available = value;
                availableTextView.setText("Available. "+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sheetBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(fue1RadioButton.isChecked()){
                    if(available>0){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Intent bookingIntent = new Intent( getContext() , BookingActivity.class);
                        startActivity(bookingIntent);
                    }else {
                        Toast.makeText(getContext(), "No available Parking in Garage", Toast.LENGTH_SHORT).show();

                    }

                }else{
                    Toast.makeText(getContext(), "Select Your Garage", Toast.LENGTH_SHORT).show();
                }

/*                Fragment fragment = new WalletFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();*/
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
//                    mMap.clear();
                    myLocation=location;
                    LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
                    myLocationLatLng = latLng;
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here ");
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 14));
                    mMap.addMarker(markerOptions).showInfoWindow();

                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == R_Code){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }else {
            Toast.makeText(getContext(), "denied", Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////////////////////////////
    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if(Start==null || End==null) {
            Toast.makeText(getContext(),"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyAvFKmIZvBUSF7WbLEOktiltb096f0C3y0")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(getContext(),"Unable to get location",Toast.LENGTH_LONG).show();
//        Findroutes(start,end);
    }


    @Override
    public void onRoutingStart() {
        Toast.makeText(getContext(),"Finding Route...",Toast.LENGTH_LONG).show();

    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        Toast.makeText(getContext(),"Finding Success...",Toast.LENGTH_LONG).show();


        mMap.clear();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }
        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
    }
    @Override
    public void onRoutingCancelled() {
//        Findroutes(garage1,garage2);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Findroutes(garage1,garage2);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
    }

}