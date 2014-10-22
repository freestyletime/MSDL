Android File DownloadManager
========================================
###Multi - Single Download Simple Framework
#####This is a file download simple framework based on android and you can use it to download and manage any file.
***
##### feature:
      1. Support task Automatic queue;
      2. Support to set running task size;
      3. Support to set repeat time;
      4. Support to pause and resume downloads;
      5. Support to cancel the task by unique id;
      6. Support two data return style, User can select annotation or callback to return the result;
      7. Have no dependency;
      8. Light weight;
      
      In a word, you can manage your downloads by the unique id.
##### necessary user-permission in AndroidManifest.xml 
      <uses-permission android:name="android.permission.INTERNET"/>
      <uses-permission android:name="android.permission.WAKE_LOCK"/>
      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
      <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
***
###example
```java
//application
public class MSDLTestApplication extends Application {
    //create the DownLoader in a global class(application/service etc..)
    //In order to keep the background thread is not killed when your app exit
    public static DownLoader downLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        downLoader = new DownLoadManager(this);
    }
}

//activity
public class MSDLTestActivity extends Activity implements View.OnClickListener{
      
      ...
      
      MSDLTestApplication application;
      DownLoader downLoader;
      
      @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        application = (MSDLTestApplication) getApplication();
        downLoader = application.downLoader.setBasePath(basePath);
        //If you register the object, you can use annotation to return the downloads values
        //And you can also use callback such as 
        //DownLoader.setOnDownLoadTaskListener(DownLoadTaskListener) 
        //Or DownLoader.add(String, String, DownLoadTaskListener);
        //To return the values, the both are asynchronous.
        downLoader.register(this);
      }
      
      @DownLoadCallback
      public void fun(DownLoadUserTask task){
            ...
      }
      
      @Override
      protected void onDestroy() {
        super.onDestroy();
        downLoader.unRegister(this);
    }
}
```
***
###
![](https://github.com/freestyletime/MSDL/blob/master/example/msdl.png)
***
###ProGuard configuration
#####ProGuard obfuscates method names. And the fun methods can renamed because they are accessed using reflection by annotation of `DownLoadCallback`. Use the following snip in your ProGuard configuration file (proguard.cfg):
      -keepclassmembers class ** {
            public void **(cn.christian.msdl.DownLoadUserTask);
      }
      -keepclassmembers class ** {
            void **(cn.christian.msdl.DownLoadUserTask);
      }
      -keepclassmembers class * {
            @cn.christian.msdl.DownLoadCallback *;
      }
***
###Copyright and License
#####Code released under the BSD license.
