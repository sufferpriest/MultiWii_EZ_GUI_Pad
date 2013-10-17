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
    along with mContext program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.aux_pid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class PadAUXActivity {
    public Activity mContext;
    public App app;
	private boolean killme = false;

	Handler mHandler = new Handler();

	TextView TextViewInfo;
	TableLayout tableL;
	boolean isCheckBoxCreated = false;
	
	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			SetActiveStates();
			app.Frequentjobs();

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);
			Log.d(app.TAG, "loop " + mContext.getClass().getName());

		}
	};

	private String GetTextValueOfAux(float rcAux) {
		if (rcAux > 1600)
			return "H";
		if (rcAux < 1400)
			return "L";
		if (rcAux >= 1400 && rcAux <= 1600)
			return "M";
		return null;
	}

	void SetAllChexboxes() {
		for (int i = 0; i < app.mw.buttonCheckboxLabel.length; i++) {
			for (int j = 0; j < 12; j++) {
				SetCheckbox(i * 100 + j, app.mw.Checkbox[i][j]);
			}
		}
	}

	public void ReadOnClick() {
		app.mw.SendRequestMSP_BOX();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		SetAllChexboxes();

	}

	public void SetOnClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(mContext.getString(R.string.Continue)).setCancelable(false).setPositiveButton(mContext.getString(R.string.Yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				for (int i = 0; i < app.mw.buttonCheckboxLabel.length; i++) {
					for (int j = 0; j < 12; j++) {
						app.mw.Checkbox[i][j] = GetCheckbox(i * 100 + j);
					}
				}
				app.mw.SendRequestMSP_SET_BOX();

				app.mw.SendRequestMSP_EEPROM_WRITE();

				Toast.makeText(mContext.getApplicationContext(), mContext.getString(R.string.Done), Toast.LENGTH_SHORT).show();

			}
		}).setNegativeButton(mContext.getString(R.string.No), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	protected void onCreate(Bundle savedInstanceState) {
		CreateGUI();

	}

	private void SetCheckbox(int ID, Boolean checked) {
		CheckBox ch = (CheckBox) mContext.findViewById(ID);
		Log.d("aaa", String.valueOf(ID) + "=" + String.valueOf(checked));
		ch.setChecked(checked);

	}

	private Boolean GetCheckbox(int ID) {
		CheckBox ch = (CheckBox) mContext.findViewById(ID);
		// Log.d("aaa", String.valueOf(ID) + "=" + String.valueOf(checked));
		// ch.setChecked(checked);
		return ch.isChecked();
	}

	private void SetActive(int ID, Boolean active) {
		TextView t = (TextView) mContext.findViewById(ID);
		if (active) {
			t.setBackgroundColor(Color.GREEN);
		} else {
			t.setBackgroundColor(mContext.getResources().getColor(R.color.tittle));
		}
	}

	public void SetActiveStates() {
		CreateGUI();
		for (int j = 0; j < app.mw.buttonCheckboxLabel.length; j++) {
			SetActive(250 + j, app.mw.ActiveModes[j]);
		}
		TextViewInfo.setText("Aux1:" + GetTextValueOfAux(app.mw.rcAUX1) + " " + String.valueOf((int) app.mw.rcAUX1) + " Aux2:" + GetTextValueOfAux(app.mw.rcAUX2) + " " + String.valueOf((int) app.mw.rcAUX2) + " Aux3:" + GetTextValueOfAux(app.mw.rcAUX3) + " " + String.valueOf((int) app.mw.rcAUX3) + " Aux4:" + GetTextValueOfAux(app.mw.rcAUX4) + " " + String.valueOf((int) app.mw.rcAUX4));
	}

	public void CreateGUI() {
		if (tableL == null) {
			TextViewInfo = new TextView(mContext);
			TextViewInfo.setGravity(Gravity.CENTER);
			TextViewInfo.setBackgroundResource(R.drawable.frame);
			TextViewInfo.setTextAppearance(mContext.getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Small);
	
			HorizontalScrollView horizontalSV = new HorizontalScrollView(mContext);
			ScrollView verticalSV = new ScrollView(mContext);
	
			LinearLayout linearL = new LinearLayout(mContext);
	
			tableL = new TableLayout(mContext);
			tableL.setBackgroundResource(R.drawable.frame);
	
			// add info text
			TextView TVClickForInfo = new TextView(mContext);
			TVClickForInfo.setText(mContext.getString(R.string.ClickHereForMoreInfo));
			TVClickForInfo.setClickable(true);
			TVClickForInfo.setTextColor(mContext.getResources().getColor(R.color.link));
			TVClickForInfo.setTag("http://www.multiwii.com/forum/viewtopic.php?f=16&t=3011&p=30010&hilit=combining#p30010");
			TVClickForInfo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					app.OpenInfoOnClick(v);
				}
			});
	
			// adding tittles
			TableRow r1 = new TableRow(mContext);
			TextView tv1 = new TextView(mContext);
			for (int zz = 0; zz < 4; zz++) {
				if (zz == 0) {
					tv1 = new TextView(mContext);
					tv1.setText("");
					r1.addView(tv1);
				}
	
				tv1 = new TextView(mContext);
				tv1.setText("");
				tv1.setBackgroundColor(mContext.getResources().getColor(R.color.tittle));
				tv1.setTextColor(mContext.getResources().getColor(R.color.tittleText));
				r1.addView(tv1);
	
				tv1 = new TextView(mContext);
				tv1.setText("AUX " + String.valueOf(zz + 1));
				tv1.setBackgroundColor(mContext.getResources().getColor(R.color.tittle));
				tv1.setTextColor(mContext.getResources().getColor(R.color.tittleText));
				r1.addView(tv1);
	
				tv1 = new TextView(mContext);
				tv1.setText("");
				tv1.setBackgroundColor(mContext.getResources().getColor(R.color.tittle));
				tv1.setTextColor(mContext.getResources().getColor(R.color.tittleText));
				r1.addView(tv1);
	
				tv1 = new TextView(mContext);
				tv1.setText("");
				r1.addView(tv1);
			}
			tableL.addView(r1);
	
			r1 = new TableRow(mContext);
			for (int zz = 0; zz < 4; zz++) {
				if (zz == 0) {
					tv1 = new TextView(mContext);
					tv1.setText(" ");
					tv1.setGravity(Gravity.CENTER);
					r1.addView(tv1);
				}
	
				tv1 = new TextView(mContext);
				tv1.setText("L");
				tv1.setGravity(Gravity.CENTER);
				tv1.setBackgroundColor(mContext.getResources().getColor(R.color.smalltittle));
				tv1.setTextColor(mContext.getResources().getColor(R.color.smalltittleText));
				r1.addView(tv1);
	
				tv1 = new TextView(mContext);
				tv1.setText("M");
				tv1.setGravity(Gravity.CENTER);
				tv1.setBackgroundColor(mContext.getResources().getColor(R.color.smalltittle));
				tv1.setTextColor(mContext.getResources().getColor(R.color.smalltittleText));
				r1.addView(tv1);
	
				tv1 = new TextView(mContext);
				tv1.setText("H");
				tv1.setGravity(Gravity.CENTER);
				tv1.setBackgroundColor(mContext.getResources().getColor(R.color.smalltittle));
				tv1.setTextColor(mContext.getResources().getColor(R.color.smalltittleText));
				r1.addView(tv1);
	
				if (zz < 3) {
					tv1 = new TextView(mContext);
					tv1.setText(" ");
					tv1.setGravity(Gravity.CENTER);
					r1.addView(tv1);
				}
			}
			
			tableL.addView(r1);
			// l.addView(TextViewInfo);
			linearL.addView(tableL);
			verticalSV.addView(linearL);
			horizontalSV.addView(verticalSV);

			//LinearLayout a = new LinearLayout(mContext);
			LinearLayout a = (LinearLayout) mContext.findViewById(R.id.func_aux);
			a.setOrientation(LinearLayout.VERTICAL);
			a.addView(TVClickForInfo);
			a.addView(TextViewInfo);
			a.addView(horizontalSV);

			//setContentView(a);
		}
		
		// titles end/////
		if (isCheckBoxCreated == false && app.mw.buttonCheckboxLabel.length > 0) {
			for (int j = 0; j < app.mw.buttonCheckboxLabel.length; j++) {
				TableRow r = new TableRow(mContext);
				TextView tv = new TextView(mContext);
				tv.setText(app.mw.buttonCheckboxLabel[j]);
	
				tv.setId(j + 250);
	
				tv.setBackgroundColor(mContext.getResources().getColor(R.color.tittle));
				tv.setTextColor(mContext.getResources().getColor(R.color.tittleText));
				r.addView(tv);
	
				int ID = 100 * j - 1;
				for (int i = 1; i <= 15; i++) {
					CheckBox c = new CheckBox(mContext);
	
					if (i % 4 == 0) {
						c.setVisibility(View.INVISIBLE);
					} else {
						ID++;
						c.setId(ID);
						// c.setText(String.valueOf(ID));
					}
					r.addView(c);
				}
				tableL.addView(r);
				isCheckBoxCreated = true;
			}
		}
	}

	protected void onResume() {
		killme = false;
		//mHandler.postDelayed(update, app.RefreshRate);
		ReadOnClick();

	}

	protected void onPause() {
		mHandler.removeCallbacks(null);
		killme = true;
	}

	// /////menu////////
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = mContext.getMenuInflater();
		inflater.inflate(R.menu.menu_aux, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuReadCheckbox) {
			ReadOnClick();
			return true;
		}

		// if (item.getItemId() == R.id.MenuSetCheckbox) {
		// SetOnClick();
		// return true;
		// }

		if (item.getItemId() == R.id.MenuSaveCheckbox) {
			SetOnClick();
			return true;
		}

		return false;
	}

	// ///menu end//////
    public void show() {
    	onCreate(null);
    	onResume();
    }
}
