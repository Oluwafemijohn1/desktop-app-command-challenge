import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method
import sun.misc.Unsafe

@Composable
@Preview
fun App() {
    var output by remember { mutableStateOf("Hello, World!") }
    var pid by remember { mutableStateOf<Long>(0) }

    MaterialTheme {
        Column {
            Button(onClick = {
                output = "Start, server"
                try {
                    val process = Runtime.getRuntime().exec("java -jar /Users/abiodunonitiju/Desktop/Projects/HibernateDemo/SprintBootJspDemo/target/SprintBootJspDemo-0.0.1-SNAPSHOT.war")
                    pid = getUnixPid(process)
                    println("Process ID: $pid")
                    output = "Command executed successfully with pid: $pid"

                } catch (ex: Exception) {
                    output = "An error occurred: ${ex.message}"
                    println("")
                }
            }) {

                Text(output)
            }

            Button(onClick = {
                try {
                    Runtime.getRuntime().exec("kill $pid")
                    output = "Restart Server"
                } catch (ex: Exception){
                    output = "An error occurred: ${ex.message}"
                }
            }){
                Text("Stop")
            }
        }
    }
}



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

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
