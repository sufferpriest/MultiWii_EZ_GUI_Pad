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
package com.ezio.multiwii.advanced;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.radio.StickView;

public class PadControlActivity {

	private boolean		killme		= false;
    public Activity mContext;
    public App					app;
	Handler				mHandler	= new Handler();
	int s1_x=0, s1_y=0, s2_x=0, s2_y=0;
	StickView			s1;
	StickView			s2;

	SeekBar				SeekBar1;
	SeekBar				SeekBar2;
	SeekBar				SeekBar3;
	SeekBar				SeekBar4;
	
	SeekBar				SeekBar5;
	SeekBar				SeekBar6;
	SeekBar				SeekBar7;
	SeekBar				SeekBar8;
	int[]				CH8			= { 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500 };

	private Runnable	update		= new Runnable() {
										@Override
										public void run() {

											//app.mw.ProcessSerialData(app.loggingON);

											/*Log.d("aaa","Throttle="+String.valueOf(app.mw.rcThrottle));
											Log.d("aaa","Yaw="+String.valueOf(app.mw.rcYaw));;
											Log.d("aaa","Pitch="+String.valueOf(app.mw.rcPitch));
											Log.d("aaa","Roll="+String.valueOf(app.mw.rcRoll));
											*/
											
											/*if (app.RadioMode == 2) {
												s1.SetPosition(app.mw.rcYaw, app.mw.rcThrottle);
												s2.SetPosition(app.mw.rcRoll, app.mw.rcPitch);
											}

											if (app.RadioMode == 1) {
												s1.SetPosition(app.mw.rcYaw, app.mw.rcPitch);
												s2.SetPosition(app.mw.rcRoll, app.mw.rcThrottle);
											}*/

											// * 0rcRoll 1rcPitch 2rcYaw
											// 3rcThrottle 4rcAUX1 5rcAUX2
											// 6rcAUX3 7rcAUX4
//
											CH8[4] = SeekBar1.getProgress() + 1000;
											CH8[5] = SeekBar2.getProgress() + 1000;
											CH8[6] = SeekBar3.getProgress() + 1000;
											CH8[7] = SeekBar4.getProgress() + 1000;

											//app.mw.SendRequestSetRawRC(CH8);
											//app.Frequentjobs();

											//app.mw.SendRequest();
											app.mw.SendRequestMSP_SET_RAW_RC(CH8);
											if (!killme)
												mHandler.postDelayed(update, 70);

										}
									};

	protected void onCreate(Bundle savedInstanceState) {        
		SharedPreferences  sp = mContext.getSharedPreferences("config", 0);
		final Editor sp_editor = sp.edit();
		
		s1 = (StickView) mContext.findViewById(R.id.stickView1);
		s1.topText = mContext.getString(R.string.MaxThrottle);
		s1.rightText = mContext.getString(R.string.Yaw);
		
		s1.y_init = 1000;
		
		s2 = (StickView) mContext.findViewById(R.id.stickView2);
		s2.topText = mContext.getString(R.string.Pitch);
		s2.rightText = mContext.getString(R.string.Roll);
		
		SeekBar.OnSeekBarChangeListener barChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
				CH8[4] = SeekBar1.getProgress() + 1000;
				CH8[5] = SeekBar2.getProgress() + 1000;
				CH8[6] = SeekBar3.getProgress() + 1000;
				CH8[7] = SeekBar4.getProgress() + 1000;
				app.mw.SendRequestMSP_SET_RAW_RC(CH8);
				//mHandler.removeCallbacks(null);
				//mHandler.post(update);
				
				sp_editor.putInt("aux1", SeekBar1.getProgress());
				sp_editor.putInt("aux2", SeekBar2.getProgress());
				sp_editor.putInt("aux3", SeekBar3.getProgress());
				sp_editor.putInt("aux4", SeekBar4.getProgress());
				
				sp_editor.putInt("thro_tuning", SeekBar5.getProgress());
				sp_editor.putInt("yaw_tuning", SeekBar6.getProgress());
				sp_editor.putInt("roll_tuning", SeekBar7.getProgress());
				sp_editor.putInt("pitch_tuning", SeekBar8.getProgress());
				
				sp_editor.commit();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
		};
		
		SeekBar1 = (SeekBar) mContext.findViewById(R.id.seekBar1);
		SeekBar2 = (SeekBar) mContext.findViewById(R.id.seekBar2);
		SeekBar3 = (SeekBar) mContext.findViewById(R.id.seekBar3);
		SeekBar4 = (SeekBar) mContext.findViewById(R.id.seekBar4);
		
		SeekBar5 = (SeekBar) mContext.findViewById(R.id.seekBar5);
		SeekBar6 = (SeekBar) mContext.findViewById(R.id.seekBar6);
		SeekBar7 = (SeekBar) mContext.findViewById(R.id.seekBar7);
		SeekBar8 = (SeekBar) mContext.findViewById(R.id.seekBar8);
		
		SeekBar1.setProgress(sp.getInt("aux1", 500));
		SeekBar2.setProgress(sp.getInt("aux2", 500));
		SeekBar3.setProgress(sp.getInt("aux3", 500));
		SeekBar4.setProgress(sp.getInt("aux4", 500));
		
		SeekBar5.setProgress(sp.getInt("thro_tuning", 300));
		SeekBar6.setProgress(sp.getInt("yaw_tuning", 300));
		SeekBar7.setProgress(sp.getInt("roll_tuning", 300));
		SeekBar8.setProgress(sp.getInt("pitch_tuning", 300));
		
		SeekBar1.setOnSeekBarChangeListener(barChangeListener);
		SeekBar2.setOnSeekBarChangeListener(barChangeListener);
		SeekBar3.setOnSeekBarChangeListener(barChangeListener);
		SeekBar4.setOnSeekBarChangeListener(barChangeListener);
		SeekBar5.setOnSeekBarChangeListener(barChangeListener);
		SeekBar6.setOnSeekBarChangeListener(barChangeListener);
		SeekBar7.setOnSeekBarChangeListener(barChangeListener);
		SeekBar8.setOnSeekBarChangeListener(barChangeListener);
		
		final int SENSIBILITY = 5;
		s1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
                        s1_x = (int) event.getX(); s1_y = (int) event.getY();
        				//mHandler.removeCallbacks(null);
        				//mHandler.post(update);
						break;
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_OUTSIDE:
					case MotionEvent.ACTION_CANCEL:
						s1.SetPosition((s1.InputX(event.getX())), s1.InputY(event.getY())); // TODO
																							// REMOVE
						CH8[3] = (int) s1.InputY(event.getY()) + SeekBar5.getProgress() - (SeekBar5.getMax() / 2); // throttle
						CH8[2] = (int) s1.InputX(event.getX()) + SeekBar6.getProgress() - (SeekBar6.getMax() / 2); // yaw
						boolean isSend = false;
						
						if ( Math.abs(event.getX() - s1_x) > SENSIBILITY) {
							s1_x = (int) event.getX();
							isSend = true;
						}
						
						if ( Math.abs(event.getY() - s1_y) > SENSIBILITY) {
							s1_y = (int) event.getY();
							isSend = true;
						}
						if (isSend)	app.mw.SendRequestMSP_SET_RAW_RC(CH8);
						break;
					case MotionEvent.ACTION_UP:
						s1.SetPosition(1500, s1.InputY(event.getY()));// TODO
						// REMOVE
						CH8[3] = (int) s1.InputY(event.getY()) + SeekBar5.getProgress() - (SeekBar5.getMax() / 2); // throttle
						CH8[2] = 1500 + SeekBar6.getProgress() - (SeekBar6.getMax() / 2); // yaw
						app.mw.SendRequestMSP_SET_RAW_RC(CH8);
						//mHandler.removeCallbacks(null);
						break;
				}
				return true;
			}
		});

		s2.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//mHandler.removeCallbacks(null);
						//mHandler.post(update);
						break;
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_OUTSIDE:
					case MotionEvent.ACTION_CANCEL:
						 s2.SetPosition((s2.InputX(event.getX())),
						 s2.InputY(event.getY()));
						CH8[1] = (int) s1.InputY(event.getY()) + SeekBar8.getProgress() - (SeekBar8.getMax() / 2); // pitch
						CH8[0] = (int) s1.InputX(event.getX()) + SeekBar7.getProgress() - (SeekBar7.getMax() / 2);// roll
						boolean isSend = false;
						
						if ( Math.abs(event.getX() - s2_x) > SENSIBILITY) {
							s2_x = (int) event.getX();
							isSend = true;
						}
						
						if ( Math.abs(event.getY() - s2_y) > SENSIBILITY) {
							s2_y = (int) event.getY();
							isSend = true;
						}
						if (isSend)	app.mw.SendRequestMSP_SET_RAW_RC(CH8);
						break;
					case MotionEvent.ACTION_UP:
						 s2.SetPosition(1500, 1500);
						CH8[1] = 1500 + SeekBar8.getProgress() - (SeekBar8.getMax() / 2);
						CH8[0] = 1500 + SeekBar7.getProgress() - (SeekBar7.getMax() / 2);
						app.mw.SendRequestMSP_SET_RAW_RC(CH8);
						//mHandler.removeCallbacks(null);
						break;
				}
				return true;
			}
		});

	}

	private static int MENU_AUX = 0;
	private static int MENU_TUNING = 1;
	private static int MENU_HIDEALL = 2;
	private static int MENU_ARM = 3;
	private static int MENU_DISARM = 4;
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_HIDEALL, 0, "HideAll").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
		menu.add(0, MENU_AUX, 1, "AUX").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
		menu.add(0, MENU_TUNING, 2, "TUNING").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
		menu.add(0, MENU_ARM, 3, "ARM").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
		menu.add(0, MENU_DISARM, 4, "DISARM").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_AUX) {
			mContext.findViewById(R.id.seektable_tuning).setVisibility(View.GONE);
			mContext.findViewById(R.id.seektable_aux).setVisibility(View.VISIBLE);
			/*LinearLayout op = (LinearLayout)findViewById(R.id.op);
			LayoutParams lp = op.getLayoutParams();
			lp.height = lp.FILL_PARENT;
			op.setLayoutParams(lp);*/
		} else if (item.getItemId() == MENU_TUNING) {
			mContext.findViewById(R.id.seektable_tuning).setVisibility(View.VISIBLE);
			mContext.findViewById(R.id.seektable_aux).setVisibility(View.GONE);
		} else if (item.getItemId() == MENU_HIDEALL) {
			mContext.findViewById(R.id.seektable_tuning).setVisibility(View.GONE);
			mContext.findViewById(R.id.seektable_aux).setVisibility(View.GONE);
		} else if (item.getItemId() == MENU_ARM) {

		} else if (item.getItemId() == MENU_DISARM) {
		
		}
		return true;
	}
	
	public void doDisaArm(View v) {
		mHandler.removeCallbacks(null);	
		s1.SetPosition(1500, 1000);
		s2.SetPosition(1000, 1500);
		// REMOVE
		CH8[3] = 1000; // throttle
		CH8[2] = 1500; // yaw
		CH8[1] = 1500; // pitch
		CH8[0] = 1000;// roll
		app.mw.SendRequestMSP_SET_RAW_RC(CH8);
		Toast.makeText(mContext.getApplicationContext(), "Disarming...", Toast.LENGTH_SHORT).show();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mHandler.removeCallbacks(null);	
				s1.SetPosition(1500, 1000);
				s2.SetPosition(1500, 1500);
				// REMOVE
				CH8[3] = 1000; // throttle
				CH8[2] = 1500; // yaw
				CH8[1] = 1500; // pitch
				CH8[0] = 1500;// roll
				app.mw.SendRequestMSP_SET_RAW_RC(CH8);					
				Toast.makeText(mContext.getApplicationContext(), "DisArmed", Toast.LENGTH_SHORT).show();
			}}, 2000);			
	}
	
	public void doArm(View v) {
		mHandler.removeCallbacks(null);	
		s1.SetPosition(1500, 1000);
		s2.SetPosition(2000, 1500);
		// REMOVE
		CH8[3] = 1000; // throttle
		CH8[2] = 1500; // yaw
		CH8[1] = 1500; // pitch
		CH8[0] = 2000;// roll
		app.mw.SendRequestMSP_SET_RAW_RC(CH8);
		Toast.makeText(mContext.getApplicationContext(), "Arming...", Toast.LENGTH_SHORT).show();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mHandler.removeCallbacks(null);	
				s1.SetPosition(1500, 1000);
				s2.SetPosition(1500, 1500);
				// REMOVE
				CH8[3] = 1000; // throttle
				CH8[2] = 1500; // yaw
				CH8[1] = 1500; // pitch
				CH8[0] = 1500;// roll
				app.mw.SendRequestMSP_SET_RAW_RC(CH8);					
				Toast.makeText(mContext.getApplicationContext(), "Armed", Toast.LENGTH_SHORT).show();
			}}, 2000);		
	}
	
	protected void onResume() {
		app.ForceLanguage();
		app.Say(mContext.getString(R.string.Control));
		killme = false;
		//mHandler.postDelayed(update, app.RefreshRate);

	}

	protected void onPause() {
		mHandler.removeCallbacks(null);
		killme = true;
	}

	public void show() {
	    onCreate(null);
	    onResume();
		View v1 = mContext.findViewById(R.id.pad_control);
		v1.setVisibility(View.VISIBLE);
	}
	
	public void doCtrlQuit(View v) {
		onPause();
		View v1 = mContext.findViewById(R.id.pad_control);
		v1.setVisibility(View.GONE);
	}
	
	public void doUpdate() {
		View v1 = mContext.findViewById(R.id.pad_control);
		if (v1.getVisibility() == View.GONE) return;
		
		CH8[4] = SeekBar1.getProgress() + 1000;
		CH8[5] = SeekBar2.getProgress() + 1000;
		CH8[6] = SeekBar3.getProgress() + 1000;
		CH8[7] = SeekBar4.getProgress() + 1000;

		app.mw.SendRequestMSP_SET_RAW_RC(CH8);		
	}
}
