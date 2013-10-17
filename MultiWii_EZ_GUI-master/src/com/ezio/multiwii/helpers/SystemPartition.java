package com.ezio.multiwii.helpers; 

 import java.io.DataInputStream; 
 import java.io.File; 
 import java.io.FileInputStream; 
 import java.io.IOException; 
  
 import android.util.Log; 
  
 public class SystemPartition { 
     private static final String TAG = "SystemMount"; 
     private static String TMP_PATH = "/sdcard/mount.txt"; 
     private static String mMountPiont = null; 
     private static boolean mWriteable = false; 
      
     private SystemPartition() { 
         Log.i(TAG, "new SystemMount()"); 
     } 
      
     private static class SystemPartitionHolder { 
         private static SystemPartition instance = new SystemPartition(); 
     } 
      
     public SystemPartition getInstance() { 
         return SystemPartitionHolder.instance; 
     } 
      
     public static String getSystemMountPiont() { 
         DataInputStream dis = null; 
         if (mMountPiont == null) {  
             try { 
                 RootCmd.execRootCmd("mount > " + TMP_PATH); 
 //              Runtime.getRuntime().exec("mount > " + TMP_PATH); 
                  
                 dis = new DataInputStream(new FileInputStream(TMP_PATH)); 
                  
                 String line = null; 
                 int index = -1; 
                 while ( (line = dis.readLine()) != null ) { 
                     index = line.indexOf(" /system "); 
                     if (index > 0) { 
                         mMountPiont = line.substring(0, index); 
                         if (line.indexOf(" rw") > 0) { 
                             mWriteable = true; 
                             Log.i(TAG, "/system is writeable !"); 
                         } else { 
                             mWriteable = false; 
                             Log.i(TAG, "/system is readonly !"); 
                         } 
                         break; 
                     } 
                 } 
             } catch (Exception e) { 
                 e.printStackTrace(); 
             } finally { 
                 if (dis != null) { 
                     try { 
                         dis.close(); 
                     } catch (IOException e1) { 
                         e1.printStackTrace(); 
                     } 
                     dis = null; 
                 } 
                  
                 File f = new File(TMP_PATH); 
                 if (f.exists()) { 
                     f.delete(); 
                 } 
             } 
         } 
          
         if (mMountPiont != null) { 
             Log.i(TAG, "/system mount piont: " + mMountPiont); 
         } else { 
             Log.i(TAG, "get /system mount piont failed !!!"); 
         } 
          
         return mMountPiont; 
     } 
      
     public static boolean isWriteable() { 
         mMountPiont = null; 
         getSystemMountPiont(); 
         return mWriteable; 
     } 
      
     public static void remountSystem(boolean writeable) { 
         String cmd = null; 
         getSystemMountPiont(); 
         if (mMountPiont != null && RootCmd.haveRoot()) { 
             if (writeable) { 
                 cmd = "mount -o remount,rw " + mMountPiont + " /system"; 
             } else { 
                 cmd = "mount -o remount,ro " + mMountPiont + " /system"; 
             } 
             RootCmd.execRootCmdSilent(cmd); 
              
             isWriteable(); 
         } 
     } 
 }