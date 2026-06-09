package com.usher.tactical.core.database.converter

import androidx.room.TypeConverter

/**
 * Room 类型转换器。
 * 当前所有字段使用原生类型（String/Long/Float/Boolean/Int），无需额外转换。
 * 未来若存储 JSON 数组/对象，在此添加 Gson/Moshi 转换方法。
 */
class Converters {
    // 预留：未来 JSON 字段的转换器
    // @TypeConverter
    // fun fromJson(value: String): SomeType = Gson().fromJson(value, SomeType::class.java)
    // @TypeConverter
    // fun toJson(value: SomeType): String = Gson().toJson(value)
}
