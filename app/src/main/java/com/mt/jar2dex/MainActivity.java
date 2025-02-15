package com.mt.jar2dex;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.EditText;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	private EditText path;
	private Button process;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			} else {
				initializeLogic();
			}
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		path = findViewById(R.id.path);
		process = findViewById(R.id.process);
		
		process.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View dialogView = inflater.inflate(R.layout.process_dlg, null);
				
				Spinner spinner = dialogView.findViewById(R.id.spinner1);
				
				String[] minSdkVersions = {"API 13 (Dex 035)", "API 21 (Dex 035)", "API 24 (Dex 037)", "API 26 (Dex 038)", "API 28 (Dex 039)"};
				
				ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, minSdkVersions);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				spinner.setAdapter(adapter);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setView(dialogView)
				    .setPositiveButton("OK", new AlertDialog.OnClickListener() {
					        @Override
					        public void onClick(android.content.DialogInterface dialog, int which) {
						            String selectedItem = spinner.getSelectedItem().toString();
						            
						            // Extract the MinSdk version (e.g., "21" from "API 21 (Dex 035)")
						            String start = "API ";
						            String end = " (";
						            
						            int startIndex = selectedItem.indexOf(start);
						            int endIndex = selectedItem.indexOf(end, startIndex + start.length());
						            
						            if (startIndex != -1 && endIndex != -1) {
							                String minSdkVersion = selectedItem.substring(startIndex + start.length(), endIndex);
							                String jarPath = path.getText().toString();
							                
							                Jar2Dex jar2Dex = new Jar2Dex(MainActivity.this);
							                jar2Dex.convertJarToDex(jarPath, new File(jarPath).getParent() + "/", Integer.parseInt(minSdkVersion));
							                Toast.makeText(MainActivity.this, "Selected MinSdk: " + minSdkVersion, Toast.LENGTH_SHORT).show();
							            } else {
							                Toast.makeText(MainActivity.this, "Failed to extract MinSdk version", Toast.LENGTH_SHORT).show();
							            }
						        }
					    })
				    .setNegativeButton("Cancel", null);
				
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}
	
	private void initializeLogic() {
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}