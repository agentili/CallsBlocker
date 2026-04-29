# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }

# Retrofit/OkHttp (if used)
-keepattributes Signature
-keepattributes *Annotation*

# Kotlin
-keepclassmembers class * {
    ** CREATOR;
}

# Compose
-keep class androidx.compose.** { *; }

# Keep Enum constants
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
