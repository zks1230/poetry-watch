# Wear OS ProGuard 规则

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keep class com.example.poetrywatch.data.db.entity.** { *; }
