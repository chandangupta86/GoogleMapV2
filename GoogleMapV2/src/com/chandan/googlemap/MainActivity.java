package com.chandan.googlemap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {
	  static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	  static final LatLng KIEL = new LatLng(53.551, 9.993);
	  private GoogleMap map;
	  MarkerOptions markerOptions;
	  ProgressDialog pDialog;
      
      List<LatLng> polyz;
      JSONArray array;
      
      Document document;
	  GMapV2GetRouteDirection v2GetRouteDirection;
	  
	  LatLng fromPosition;
      LatLng toPosition;
      
	 

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	   // map = ((MapFragment) getSupportFragmentManager()().findFragmentById(R.id.map)).getMap();
	   // map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(android.R.id.content)).getMap();
	    
	    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    
	    // Check if we were successful in obtaining the map.
	    if (map != null) {
	        // Setup your map...
	    } else {
	        int isEnabled = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	        if (isEnabled != ConnectionResult.SUCCESS) {
	            GooglePlayServicesUtil.getErrorDialog(isEnabled, this, 0);
	        }
	    }
	    
	    fromPosition = new LatLng(53.558, 9.927);
	      toPosition = new LatLng(53.551, 9.993);
	      
	      markerOptions = new MarkerOptions();
	      
	    Polyline line = map.addPolyline(new PolylineOptions().add(new LatLng(53.558, 9.927), new LatLng(53.551, 9.993)).geodesic(true));
	    
	    Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
	        .title("Hamburg"));
	    Marker kiel = map.addMarker(new MarkerOptions()
	        .position(KIEL)
	        .title("Kiel")
	        .snippet("Kiel is cool")
	        .icon(BitmapDescriptorFactory
	            .fromResource(R.drawable.ic_launcher)));

	    // Move the camera instantly to hamburg with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	    
	    
	    
	    v2GetRouteDirection = new GMapV2GetRouteDirection();
	      
	    GetRouteTask getRoute = new GetRouteTask();
        getRoute.execute();
	  }
	  
	  private class GetRouteTask extends AsyncTask<String, Void, String> {
          
          private ProgressDialog Dialog;
          String response = "";
          @Override
          protected void onPreExecute() {
                Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("Loading route...");
                Dialog.show();
          }

          @Override
          protected String doInBackground(String... urls) {
                //Get All Route values
                      document = v2GetRouteDirection.getDocument(fromPosition, toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
                      response = "Success";
                return response;

          }

          @Override
          protected void onPostExecute(String result) {
                map.clear();
                if(response.equalsIgnoreCase("Success")){
                ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                PolylineOptions rectLine = new PolylineOptions().width(10).color(
                            Color.RED);

                for (int i = 0; i < directionPoint.size(); i++) {
                      rectLine.add(directionPoint.get(i));
                }
                // Adding route on the map
                map.addPolyline(rectLine);
                markerOptions.position(toPosition);
                markerOptions.draggable(true);
                map.addMarker(markerOptions);

                }
               
                Dialog.dismiss();
          }
    }

      /* Method to decode polyline points */
      private List<LatLng> decodePoly(String encoded) {

          List<LatLng> poly = new ArrayList<LatLng>();
          int index = 0, len = encoded.length();
          int lat = 0, lng = 0;

          while (index < len) {
              int b, shift = 0, result = 0;
              do {
                  b = encoded.charAt(index++) - 63;
                  result |= (b & 0x1f) << shift;
                  shift += 5;
              } while (b >= 0x20);
              int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
              lat += dlat;

              shift = 0;
              result = 0;
              do {
                  b = encoded.charAt(index++) - 63;
                  result |= (b & 0x1f) << shift;
                  shift += 5;
              } while (b >= 0x20);
              int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
              lng += dlng;

              LatLng p = new LatLng((((double) lat / 1E5)),
                      (((double) lng / 1E5)));
              poly.add(p);
          }

          return poly;
      }

	

	} 
