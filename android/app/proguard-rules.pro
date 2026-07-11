# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.thaqalayn.app.**$$serializer { *; }
-keepclassmembers class com.thaqalayn.app.** { *** Companion; }
-keepclasseswithmembers class com.thaqalayn.app.** { kotlinx.serialization.KSerializer serializer(...); }
