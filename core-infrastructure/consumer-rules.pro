# Consumer ProGuard rules for core-infrastructure module
# These rules will be applied to any module that depends on this module

# Keep public API classes
-keep public class com.ch.ktorsample.core.** { public *; }

# Keep Room entities and DAOs
-keep class com.ch.ktorsample.core.database.entities.** { *; }
-keep class com.ch.ktorsample.core.database.dao.** { *; }
-keep class com.ch.ktorsample.core.database.AppDatabase { *; }

# Keep network classes
-keep class com.ch.ktorsample.core.network.** { *; }

# Keep security classes
-keep class com.ch.ktorsample.core.security.** { *; }
