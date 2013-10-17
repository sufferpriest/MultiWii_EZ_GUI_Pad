/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ezio.multiwii.R;
import com.ezio.multiwii.about.AboutActivity;
import com.ezio.multiwii.about.InfoActivity;
import com.ezio.multiwii.advanced.AdvancedActivity;
import com.ezio.multiwii.advanced.ControlActivity;
import com.ezio.multiwii.advanced.PadControlActivity;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.aux_pid.AUXActivity;
import com.ezio.multiwii.aux_pid.PIDActivity;
import com.ezio.multiwii.aux_pid.PadAUXActivity;
import com.ezio.multiwii.aux_pid.PadPIDActivity;
import com.ezio.multiwii.config.ConfigActivity;
import com.ezio.multiwii.dashboard.CompassView;
import com.ezio.multiwii.dashboard.Dashboard1Activity;
import com.ezio.multiwii.dashboard.Dashboard2Activity;
import com.ezio.multiwii.dashboard.Dashboard2View;
import com.ezio.multiwii.dashboard.PitchRollCircleView;
import com.ezio.multiwii.dashboard.PitchRollView;
import com.ezio.multiwii.dashboard.dashboard3.Dashboard3Activity;
import com.ezio.multiwii.dashboard.dashboard3.Dashboard3GoogleMapActivity;
import com.ezio.multiwii.frsky.FrskyActivity;
import com.ezio.multiwii.gps.GPSActivity;
import com.ezio.multiwii.gps.MOCK_GPS_Service;
import com.ezio.multiwii.gps.PadGPSActivity;
import com.ezio.multiwii.graph.GraphsActivity;
import com.ezio.multiwii.graph.PadGraphsActivity;
import com.ezio.multiwii.helpers.Functions;
import com.ezio.multiwii.log.LogActivity;
import com.ezio.multiwii.map.MapActivityMy;
import com.ezio.multiwii.mapoffline.MapOfflineActivityMy;
import com.ezio.multiwii.motors.MotorsActivity;
import com.ezio.multiwii.other.OtherActivity;
import com.ezio.multiwii.other.PadOtherCaliActivity;
import com.ezio.multiwii.radio.RadioActivity;
import com.ezio.multiwii.radio.StickView;
import com.ezio.multiwii.raw.RawDataActivity;
import com.ezio.multiwii.waypoints.WaypointActivity;
import com.viewpagerindicator.TitlePageIndicator;

public class PadMainMultiWiiActivity extends Activity {

	private boolean killme = false;

	App app;

	TextView TVinfo;

	private Handler mHandler = new Handler();

	ActionBarSherlock actionBar;

	PadControlActivity control;
	Dashboard2View dashboard2;
	PadPIDActivity pidconfig;
	PadOtherCaliActivity otherCali;
	PadAUXActivity auxfunc;
	PadGraphsActivity graphs;
	PadGPSActivity gps;
	
	public static final String MY_PUBLISHER_ID = "a15030365bc09b4";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("aaa", "MAIN ON CREATE");
		//requestWindowFeature(Window.FEATURE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.pad_main_layout);

		app = (App) getApplication();

		//getSupportActionBar().setDisplayShowTitleEnabled(false);

		/*if (app.AppStartCounter % 10 == 0 && app.DonateButtonPressed == 0) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), InfoActivity.class));
		}*/

		app.AppStartCounter++;
		app.SaveSettings(true);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onResume() {
		Log.d("aaa", "MAIN ON RESUME");
		app.ForceLanguage();
		super.onResume();

		killme = false;

		String app_ver = "";
		int app_ver_code = 0;
		try {
			app_ver = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			app_ver_code = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		if (app.commMW.Connected || app.commFrsky.Connected) {

			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}

			mHandler.postDelayed(update, 100);
			// Log.d(BT_old.TAG, "OnResume if connected");

		}
		if (auxfunc == null) {
			auxfunc = new PadAUXActivity();
			auxfunc.mContext = this;
			auxfunc.app = app;
			auxfunc.show();
		}
		
		if (graphs == null) {
			graphs = new PadGraphsActivity();
			graphs.mContext = this;
			graphs.app = app;
		}		
		graphs.show();
		
		if (gps == null) {
			gps = new PadGPSActivity();
			gps.mContext = this;
			gps.app = app;
			gps.show();
		}
	}

	@Override
	public void onPause() {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		super.onPause();
	}

	public void Close(View v) {
		try {
			mHandler.removeCallbacksAndMessages(null);
			app.commMW.Close();
			app.commFrsky.Close();
		}

		catch (Exception e) {

		}

	}

	public void Connect(String MacAddress) {
		if (app.CommunicationTypeMW == App.COMMUNICATION_TYPE_SERIAL_FTDI || app.CommunicationTypeMW == App.COMMUNICATION_TYPE_SERIAL_OTHERCHIPS) {
			app.commMW.Connect(app.SerialPortBaudRateMW);
		}

		if (app.CommunicationTypeMW == App.COMMUNICATION_TYPE_BT) {
			if (!app.MacAddress.equals("")) {
				app.commMW.Connect(app.MacAddress);
				app.Say(getString(R.string.menu_connect));
			} else {
				Toast.makeText(getApplicationContext(), "Wrong MAC address. Go to Config and select correct device", Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {
			}
		}
	}

	public void ConnectFrsky(String MacAddress) {
		if (app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_SERIAL_FTDI) {
			app.commMW.Connect(app.SerialPortBaudRateFrSky);
		}

		if (app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_BT) {
			if (!app.MacAddressFrsky.equals("")) {
				app.commFrsky.Connect(app.MacAddressFrsky);
				app.Say(getString(R.string.Connect_frsky));
			} else {
				Toast.makeText(getApplicationContext(), "Wrong MAC address. Go to Config and select correct device", Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}
		}
	}

	public void ConfigOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), ConfigActivity.class));
	}
	
	int update_sum = 0;
	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			//setSupportProgress((int) Functions.map(app.frskyProtocol.TxRSSI, 0, 110, 0, 10000));

			updateDashboard2();
			updateRadio();
			updateAuxFunc();
			update_sum ++;
			
			//performance is low, so comment this
			/*if ((update_sum % 10) == 0) {
				updateDashboard1();
				updateGPS();
			}*/
			
			app.Frequentjobs();
			
			updateGraphs();
			
			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());
		}

	};
	
	PitchRollView PRVp;
	PitchRollView PRVr;
	CompassView compass;
	CompassView myCompass;
	PitchRollCircleView pitchRollCircle;
	TextView baro;

	TextView BattVoltageTV;
	TextView PowerSumTV;

	float myAzimuth = 0;
	void updateDashboard1() {

		if (PRVp == null) {
		PRVp = (PitchRollView) findViewById(R.id.PRVp);
		PRVp.SetColor(Color.GREEN);

		PRVr = (PitchRollView) findViewById(R.id.PRVr);
		PRVr.SetColor(Color.GREEN);

		pitchRollCircle = (PitchRollCircleView) findViewById(R.id.PitchRollCircle);
		pitchRollCircle.SetColor(Color.GREEN);

		compass = (CompassView) findViewById(R.id.Mag);
		compass.SetColor(Color.GREEN, Color.YELLOW);

		myCompass = (CompassView) findViewById(R.id.CompassView02);
		myCompass.SetColor(Color.GRAY, Color.LTGRAY);
		myCompass.SetText("N");

		baro = (TextView) findViewById(R.id.textViewBaro);
		BattVoltageTV = (TextView) findViewById(R.id.TextViewBattVoltage);
		PowerSumTV = (TextView) findViewById(R.id.TextViewPowerSum);
		}
		
		myAzimuth = (float) (app.sensors.Heading);
		if (app.D) {
			app.mw.angy = app.sensors.Pitch;
			app.mw.angx = app.sensors.Roll;
		}

		PRVp.SetAngle(app.mw.angy);
		PRVr.SetAngle(app.mw.angx);

		pitchRollCircle.SetRollPitch(app.mw.angx, app.mw.angy);

		if (app.MagMode == 1) {
			compass.SetHeading(-app.mw.head);
			compass.SetText("");

		} else {
			compass.SetHeading(myAzimuth - app.mw.head);
			compass.SetText("FRONT");
		}

		myCompass.SetHeading(myAzimuth);

		baro.setText(String.format("%.2f", app.mw.alt));
		BattVoltageTV.setText(String.valueOf((float) (app.mw.bytevbat / 10.0)));
		PowerSumTV.setText(String.valueOf(app.mw.pMeterSum));		
	}
	
	void updateDashboard2() {
		String state = "";
		for (int i = 0; i < app.mw.CHECKBOXITEMS; i++) {
			if (app.mw.ActiveModes[i]) {
				state += " " + app.mw.buttonCheckboxLabel[i];
			}
		}

		int a = 1; // used for reverce roll
		if (app.ReverseRoll) {
			a = -1;
		}

		// float gforce = (float) Math.sqrt(app.mw.ax * app.mw.ax +
		// app.mw.ay * app.mw.ay + app.mw.az * app.mw.az) / app.mw._1G;
		if (dashboard2 == null) dashboard2 = (Dashboard2View)findViewById(R.id.dashboard2);
		dashboard2.Set(app.mw.GPS_numSat, app.mw.GPS_distanceToHome, app.mw.GPS_directionToHome, app.mw.GPS_speed, app.mw.GPS_altitude, app.mw.alt, app.mw.GPS_latitude, app.mw.GPS_longitude, -app.mw.angy, a * app.mw.angx, app.mw.head, app.mw.vario/100f, state, app.mw.bytevbat, app.mw.pMeterSum, app.mw.intPowerTrigger, app.frskyProtocol.TxRSSI, app.frskyProtocol.RxRSSI);
	}
	
	StickView SV1;
	StickView SV2;
	ProgressBar pb1;
	ProgressBar pb2;
	ProgressBar pb3;
	ProgressBar pb4;
	TextView TextViewRadioInfo;
	void updateRadio() {
        if (SV1 == null) {
		SV1 = (StickView) findViewById(R.id.StickView1);
		SV2 = (StickView) findViewById(R.id.StickView2);

		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		pb2 = (ProgressBar) findViewById(R.id.progressBar2);
		pb3 = (ProgressBar) findViewById(R.id.progressBar3);
		pb4 = (ProgressBar) findViewById(R.id.progressBar4);

		TextViewRadioInfo = (TextView) findViewById(R.id.textViewRadioInfo);
        }
        
		if (app.RadioMode == 2) {
			SV1.SetPosition(app.mw.rcYaw, app.mw.rcThrottle);
			SV2.SetPosition(app.mw.rcRoll, app.mw.rcPitch);
		}

		if (app.RadioMode == 1) {
			SV1.SetPosition(app.mw.rcYaw, app.mw.rcPitch);
			SV2.SetPosition(app.mw.rcRoll, app.mw.rcThrottle);
		}

		pb1.setProgress((int) (app.mw.rcAUX1 - 1000));
		pb2.setProgress((int) (app.mw.rcAUX2 - 1000));
		pb3.setProgress((int) (app.mw.rcAUX3 - 1000));
		pb4.setProgress((int) (app.mw.rcAUX4 - 1000));

		String a = "Throttle:" + String.valueOf(app.mw.rcThrottle);
		a += "\nYaw:" + String.valueOf(app.mw.rcYaw);

		a += "\nRoll:" + String.valueOf(app.mw.rcRoll);
		a += "\nPitch:" + String.valueOf(app.mw.rcPitch);

		a += "\nAUX1:" + String.valueOf(app.mw.rcAUX1);
		a += "\nAUX2:" + String.valueOf(app.mw.rcAUX2);
		a += "\nAUX3:" + String.valueOf(app.mw.rcAUX3);
		a += "\nAUX4:" + String.valueOf(app.mw.rcAUX4);

		TextViewRadioInfo.setText(a);		
	}
	
	void updateAuxFunc() {
		if (auxfunc != null) auxfunc.SetActiveStates();
	}
	
	void updateGraphs() {
		graphs.update();
	}
	
	void updateGPS() {
		gps.update();
	}
	
	public void Dashboard3OnClick(View v) {
		if (app.checkAndGuideGoogleMapLibInstall(this) == false) return;

		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		if (app.mMapEngine == App.MAP_OSM) {
		startActivity(new Intent(getApplicationContext(), Dashboard3Activity.class));
		} else {
			startActivity(new Intent(getApplicationContext(), Dashboard3GoogleMapActivity.class));
		}
	}

	public void MapOnClick(View v) {
		if (app.checkAndGuideGoogleMapLibInstall(this) == false) return;

		killme = true;
		if (app.mMapEngine == App.MAP_OSM) {
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), MapOfflineActivityMy.class));
		} else {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(), MapActivityMy.class));
		}
	}

	public void AboutOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}

	// /////menu////////
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {

			try {
				stopService(new Intent(getApplicationContext(), MOCK_GPS_Service.class));
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (app.DisableBTonExit) {
				app.commMW.Disable();
				app.commFrsky.Disable();
			}

			app.sensors.stop();
			app.mw.CloseLoggingFile();
			app.notifications.Cancel(99);
			Close(null);
			System.exit(0);
			return true;
		}

		if (item.getItemId() == R.id.menu_connect) {

			Connect(app.MacAddress);

			mHandler.postDelayed(update, 100);
			return true;
		}

		if (item.getItemId() == R.id.menu_control) {
			//startActivity(new Intent(getApplicationContext(), ControlActivity.class));
			if (control == null) {
				control = new PadControlActivity();
				control.mContext = this;
				control.app = app;
				control.show();
				Toast.makeText(this, R.string.AdvancedWarning, Toast.LENGTH_LONG).show();
				Toast.makeText(this, R.string.ControlWarning, Toast.LENGTH_LONG).show();
			}

			return true;
		}
		
		if (item.getItemId() == R.id.menuMap) {
			MapOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menuDashboard3) {
			Dashboard3OnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menu_connect_frsky) {

			ConnectFrsky(app.MacAddressFrsky);

			mHandler.postDelayed(update, 100);

			//setSupportProgressBarVisibility(true);

			return true;
		}

		if (item.getItemId() == R.id.menu_disconnect) {
			app.Say(getString(R.string.menu_disconnect));
			app.commMW.ConnectionLost = false;
			app.commFrsky.ConnectionLost = false;
			Close(null);
			return true;
		}
		
		if (item.getItemId() == R.id.menuConfig) {
			ConfigOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menuSwitchScreenMode) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			try {
				stopService(new Intent(getApplicationContext(), MOCK_GPS_Service.class));
			} catch (Exception e) {}

			if (app.DisableBTonExit) {
				app.commMW.Disable();
				app.commFrsky.Disable();
			}

			app.sensors.stop();
			app.mw.CloseLoggingFile();
			app.notifications.Cancel(99);
			Close(null);
			startActivity(new Intent(getApplicationContext(), MainMultiWiiActivity.class).putExtra("ISFROMPAD", true));
			finish();
			return true;
		}

		if (item.getItemId() == R.id.menuAbout) {
			AboutOnClick(null);
			return true;
		}

		return false;
	}

	// ///menu end//////
	public void doArm(View v) {
		if (control != null) control.doArm(v);
	}
	
	public void doDisaArm(View v) {
		if (control != null) control.doDisaArm(v);
	}
	
	public void doCtrlQuit(View v) {
		if (control != null) {
			control.doCtrlQuit(v);
			control = null;
		}
	}
	
	public void ReadOnClick(View v) {
		if (pidconfig == null) {
			pidconfig = new PadPIDActivity();
			pidconfig.app  = app;
			pidconfig.mContext = this;
			pidconfig.show();
		}
		pidconfig.ReadOnClick(v);
	}

	public void SetOnClick(View v) {
		if (pidconfig == null) {
			pidconfig = new PadPIDActivity();
			pidconfig.app  = app;
			pidconfig.mContext = this;
			pidconfig.show();
		}
		pidconfig.SetOnClick(v);
	}
	
	private void do_otherCali() {
		if (otherCali == null) {
			otherCali = new PadOtherCaliActivity();
			otherCali.app  = app;
			otherCali.mContext = this;
			otherCali.show();
		}		
	}
	
	public void BatteryVoltageRefreshOnClick(View v) {
		do_otherCali();
		otherCali.BatteryVoltageRefreshOnClick(v);
	}

	public void DeclinationTakeFromPhoneOnClick(View v) {
		do_otherCali();
		otherCali.BatteryVoltageRefreshOnClick(v);
	}

	public void MSP_MISC_CONFreadOnClick(View v) {
		do_otherCali();
		otherCali.MSP_MISC_CONFreadOnClick(v);
	}

	public void MagCalibrationOnClick(View v) {
		do_otherCali();
		otherCali.MagCalibrationOnClick(v);
	}

	public void AccCalibrationOnClick(View v) {
		do_otherCali();
		otherCali.AccCalibrationOnClick(v);
	}

	public void MSP_SET_MISC_CONF_WriteOnClick(View v) {
		do_otherCali();
		otherCali.MSP_SET_MISC_CONF_WriteOnClick(v);
	}

	public void WriteSelectSettingOnClick(View v) {
		do_otherCali();
		otherCali.WriteSelectSettingOnClick(v);
	}

	public void RXBINDOnClick(View v) {
		do_otherCali();
		otherCali.RXBINDOnClick(v);
	}

	public void SetSerialBoudRateOnClick(View v) {
		do_otherCali();
		otherCali.SetSerialBoudRateOnClick(v);
	}
	
	public void FuncAuxReadOnClick(View v) {
		if (auxfunc != null ) auxfunc.ReadOnClick();
	}
	
	public void FuncAuxSetOnClick(View v) {
		if (auxfunc != null ) auxfunc.SetOnClick();
	}
	public void GraphsPauseOnClick(View v) {
		if (graphs != null ) graphs.PauseOnClick();
	}
	
	public void GraphsShowOnClick(View v) {
		if (graphs != null ) graphs.ShowOnClick();
	}
	
	public void FollowMeCheckBoxOnClick(View v) {
		gps.FollowMeCheckBoxOnClick(v);
	}

	public void InjectGPSCheckBoxOnClick(View v) {
		gps.InjectGPSCheckBoxOnClick(v);
	}

	public void FollowHeadingCheckBoxOnClick(View v) {
		gps.FollowHeadingCheckBoxOnClick(v);
	}

	public void StartMOCKLocationServiceOnClick(View v) {
		gps.StartMOCKLocationServiceOnClick(v);
	}
	
}