Java File DownloadManager
========================================
### Multi - Single Download Simple Framework
##### This is a file download simple framework based on Java and you can use it to download and manage any file.
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
##### 
Copyright (c) 2014, Regents of the freestyletime@foxmail.com
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

*     Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
      Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
      Neither the name of the University of California, Berkeley nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
