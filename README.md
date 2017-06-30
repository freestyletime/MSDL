Java File DownloadManager
========================================
###Multi - Single Download Simple Framework
#####This is a file download simple framework based on Java and you can use it to download and manage any file.
***
##### feature:
      1. Support task Automatic queue;
      2. Support to set running task size;
      3. Support to set repeat time;
      4. Support to pause and resume downloads;
      5. Support to cancel the task by unique id;
      6. Support two data return style, User can select annotation or callback to return the result;
      7. Based on Java(HttpURLConnection) and no other dependency;
      8. Light weight;
      
      In a word, you can manage your downloads by the unique id.
##### necessary user-permission in AndroidManifest.xml 
      <uses-permission android:name="android.permission.INTERNET"/>
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
***
### ProGuard configuration
##### ProGuard obfuscates method names. And the fun methods can renamed because they are accessed using reflection by annotation of `DownLoadCallback`. Use the following snip in your ProGuard configuration file (proguard.cfg):
      -dontwarn cn.christian.**
      -keepattributes *Annotation*
      -keep class cn.christian.** { *; }
      -keepclassmembers class * {
            @cn.christian.msdl.DownLoadCallback *;
      }
***
### Copyright and License
##### Code released under the BSD license.
