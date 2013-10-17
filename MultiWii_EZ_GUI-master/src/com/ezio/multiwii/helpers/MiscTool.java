package com.ezio.multiwii.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class MiscTool {
	//
	static public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	static public void installApkFromAssets(Context context, String apkName) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		AssetManager assets = context.getAssets();
		try {
			// 当文件比较大的时候不能用这个方法 来读取Stream ss.read(buffer) = -1 我的apk大小为5M
			InputStream ss = assets.open(apkName);
			// 使用下面这个方法 没问题
			InputStream is = context.getClass().getResourceAsStream("/assets/" + apkName);

			FileOutputStream fos = context.openFileOutput(apkName, Context.MODE_PRIVATE + Context.MODE_WORLD_READABLE);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			is.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = new File(context.getFilesDir().getPath() + "/" + apkName);

		// String path = "file:///android_asset/ZXing.apk";
		// File f = new File(path);
		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);

	}

	// 检查服务是否启动
	static public boolean isStartService(Context ctx, String packageClassName) {
		ActivityManager mActivityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> currentService = mActivityManager.getRunningServices(100);
		boolean b = igrsBaseServiceIsStart(currentService, packageClassName);
		return b;
	}

	private static boolean igrsBaseServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList, String className) {
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否为平板 屏幕尺寸大于7.0为平板
	 * 
	 * @return
	 */
	public static float getScreenInches(Context context) {
		WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		// 屏幕尺寸
		double screenInches = Math.sqrt(x + y) / dm.density;

		return Math.round(screenInches);
	}

	public static boolean isPad(Context context) {
		float screenInchesround =getScreenInches(context);
		// 特定尺寸7.0则为Pad
		if (screenInchesround >= 7.0) {
			return true;
		}
		return false;
	}
}
