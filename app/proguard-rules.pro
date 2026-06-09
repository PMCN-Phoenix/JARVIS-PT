# Add project specific ProGuard rules here.
# Keep Room entities
-keep class com.usher.tactical.core.database.entity.** { *; }
# Keep SQLCipher
-keep class net.zetetic.database.sqlcipher.** { *; }
