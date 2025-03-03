package com.example.map;

import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.core.app.ActivityCompat;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import android.widget.Toast;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.*;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import com.mapbox.maps.ImageHolder;

public class MainActivity extends AppCompatActivity {
    MapView mapView;
    FloatingActionButton floatingActionButton;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        initializeLocationComponent();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
            // Implementation if needed
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        mapView = findViewById(R.id.mapView);
        floatingActionButton = findViewById(R.id.fab_recenter);  // Updated ID to match layout

        // Make sure the floating action button is initialized before calling hide()
        if (floatingActionButton != null) {
            floatingActionButton.hide();
        } else {
            // Log or handle the case where the button isn't found
            Toast.makeText(this, "Error: FloatingActionButton not found in layout", Toast.LENGTH_LONG).show();
        }

        // Load map style
        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Check permission and initialize location features
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    // Permission already granted
                    initializeLocationComponent();
                }
            }
        });

        // Setup the floating action button click listener
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recenterToUserLocation();
                }
            });
        }
    }

    private void initializeLocationComponent() {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());

        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);

        LocationPuck2D locationPuck2D = new LocationPuck2D();
        locationPuck2D.setBearingImage(ImageHolder.from(R.drawable.baseline_location_on_24));
        locationComponentPlugin.setLocationPuck(locationPuck2D);

        // Setup location tracking
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);
    }

    private void recenterToUserLocation() {
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);
        floatingActionButton.hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}