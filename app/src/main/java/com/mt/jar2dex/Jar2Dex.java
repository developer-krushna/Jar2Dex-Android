package com.mt.jar2dex;

import android.content.Context;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.app.Activity;
import com.android.dx.command.dexer.*;
import com.android.dx.command.dexer.Main.Arguments;
import com.android.dx.command.dexer.Main.ProgressCallback;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
/* Author @developer-krushna
   Comment and code enhancement by ChatGPT */
   // 16 Feb 2025
public class Jar2Dex {
	
	private Context context;
	private AlertProgress progressDialog;
	private int totalClasses = 0;
	public Jar2Dex(Context context) {
		this.context = context;
		this.progressDialog = new AlertProgress(context);
	}
	
	public void convertJarToDex(String inputJarPath, String outputDirPath, int minApiLevel) {
		// Show the progress dialog
		progressDialog.setTitle("Jar2Dex");
		progressDialog.setMessage("Processing...");
		progressDialog.setIndeterminate(false);
		progressDialog.show();
		
		// Count the total number of classes in the JAR file
		totalClasses = countClassesInJar(inputJarPath);
		
		// Run the conversion in a background thread using AsyncTask
		new DexConversionTask().execute(inputJarPath, outputDirPath, String.valueOf(minApiLevel));
	}
	
	private int countClassesInJar(String jarPath) {
		int count = 0;
		try (JarFile jarFile = new JarFile(jarPath)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					count++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	private void updateProgress(final int processed, final int total) {
		// Update the progress dialog on the UI thread
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.setMessage("Processing...");
                if(processed != 0 || total != 0){
				progressDialog.setProgress(processed, total);
                }
			}
		});
	}
	
	private void showError(final String message) {
		// Show an error message on the UI thread
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(context)
				.setTitle("Error")
				.setMessage(message)
				.setPositiveButton("OK", null)
				.show();
			}
		});
	}
	
	private void showSuccess(final String message) {
		// Show a success message on the UI thread
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(context)
				.setTitle("Success")
				.setMessage(message)
				.setPositiveButton("OK", null)
				.show();
			}
		});
	}
	
	// AsyncTask to handle background processing
	private class DexConversionTask extends AsyncTask<String, Integer, String> {
		// Use a String to return either a success message or an error message with exception details
		
		@Override
		protected String doInBackground(String... params) {
			String inputJarPath = params[0];
			String outputDirPath = params[1];
			int minApiLevel = Integer.parseInt(params[2]);
			
			try {
				// Set up arguments for the Dx tool
				String[] dxArgs = {
					"--multi-dex", // Enable multi-dex
					"--output=" + outputDirPath, // Output directory
				//	"--min-sdk-version=" + minApiLevel, // Set minimum API level
					inputJarPath // Input JAR file
				};
				
				// Create a Dx context
				DxContext dxContext = new DxContext();
				
				// Parse arguments
				Arguments arguments = new Arguments(dxContext);
				arguments.parse(dxArgs);
				
				// Create an instance of Main and set the total number of classes
				Main main = new Main(dxContext);
				
				// Define a progress callback
				ProgressCallback progressCallback = new ProgressCallback() {
					@Override
					public void onProgress(int processed) {
                        if(processed == 0){
                            publishProgress(100, 100);
                        }
						publishProgress(processed, totalClasses);
					}
				};
				
				// Set the progress callback
				main.setProgressCallback(progressCallback);
				
				// Run the Dx tool
				int result = main.runDx(arguments);
				
				if (result == 0) {
					return "Dex conversion completed successfully!";
				} else {
					return "Dex conversion failed with error code: " + result;
				}
			} catch (Exception e) {
				// Capture the exception details
				return "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// Update progress on the UI thread
			updateProgress(values[0], values[1]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// Handle the result on the UI thread
			if (result.startsWith("Error:")) {
				showError(result); // Show error dialog with exception details
			} else {
				showSuccess(result); // Show success dialog
			}
			
			// Dismiss the progress dialog
			progressDialog.dismiss();
		}
	}
}
