import sun.misc.Unsafe
import java.lang.reflect.Field

fun getUnixPid(process: Process): Long {
    val unsafe: Unsafe
    try {
        val field: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        unsafe = field.get(null) as Unsafe
    } catch (e: Exception) {
        throw IllegalStateException("Unable to access Unsafe", e)
    }

    try {
        val pidField: Field = process.javaClass.getDeclaredField("pid")
        return unsafe.getLong(process, unsafe.objectFieldOffset(pidField))
    } catch (e: Exception) {
        throw IllegalStateException("Unable to get PID", e)
    }
}