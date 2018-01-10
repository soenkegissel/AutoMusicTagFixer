# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/franco/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn javax.**
-dontwarn java.**
-dontwarn sun.security.**
-dontwarn org.**

-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep class com.gracenote.** { *; }
-keep class javax.** { *; }
-keep class java.** { *; }
-keep class org.** { *; }
-keep class sun.security.** { *; }
-keep class android.support.v7.widget.SearchView { *; }