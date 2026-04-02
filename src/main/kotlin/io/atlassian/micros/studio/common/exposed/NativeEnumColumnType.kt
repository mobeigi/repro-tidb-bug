package io.atlassian.micros.studio.common.exposed

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.Table
import kotlin.reflect.KClass

/**
 * Custom column type for native database ENUM types.
 * Generates ENUM('VALUE1', 'VALUE2', ...) SQL instead of VARCHAR.
 */
class NativeEnumColumnType<T : Enum<T>>(
    private val enumClass: KClass<T>,
) : ColumnType<T>() {
    override fun sqlType(): String {
        val enumValues =
            enumClass.java.enumConstants
                .joinToString("','", "'", "'") { it.name }
        return "ENUM($enumValues)"
    }

    override fun valueFromDB(value: Any): T =
        when (value) {
            is String -> java.lang.Enum.valueOf(enumClass.java, value)
            is Number -> enumClass.java.enumConstants[value.toInt()]
            // Handle case where database already converted to the enum type
            else -> {
                if (enumClass.isInstance(value)) {
                    @Suppress("UNCHECKED_CAST")
                    value as T
                } else {
                    error("Unexpected value type for enum: ${value.javaClass}")
                }
            }
        }

    override fun valueToDB(value: T?): Any? =
        when (value) {
            is Enum<*> -> value.name
            null -> null
        }
}

/**
 * Extension function to create a native ENUM column in database tables.
 * This generates actual ENUM('VALUE1', 'VALUE2') SQL instead of VARCHAR.
 */
inline fun <reified T : Enum<T>> Table.nativeEnum(name: String): Column<T> = registerColumn(name, NativeEnumColumnType(T::class))
