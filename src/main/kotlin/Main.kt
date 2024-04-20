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
    var backendNote by remember { mutableStateOf("Start Server") }
    var frontendNote by remember { mutableStateOf("Start client") }
    var process: Process? by remember { mutableStateOf(null) }
    var serverProcess: Process? by remember { mutableStateOf(null) }

    MaterialTheme {
        Column {
            Button(onClick = {
               if(process == null) {
                   try {
                       process = Runtime.getRuntime().exec("java -jar /Users/abiodunonitiju/Desktop/Projects/HibernateDemo/SprintBootJspDemo/target/SprintBootJspDemo-0.0.1-SNAPSHOT.war")
                       backendNote = "Server running with process id: ${getUnixPid(process!!)}..."
                       println("Backend started....")
                       println(process)
                   } catch (ex: Exception) {
                       backendNote = "An error occurred: ${ex.message}"
                       println("Error Starting Backend serve")
                   }
               } else if(process != null){
                   try {
                       Runtime.getRuntime().exec("kill ${getUnixPid(process!!)}")
                       backendNote = "Restart Server"
                       println("Server Stopped...")
                       process = null
                   } catch (ex: Exception){
                       backendNote = "An error occurred stopping Server: ${ex.message}"
                       println("Frontend starting has error")
                   }
               }
            }) {

                Text(backendNote)
            }

            Button(onClick = {
                if (serverProcess == null){
                    println("Hello Frontend")
                    try {
                         serverProcess = ProcessBuilder()
                            .command("serve", "-s", "/Users/abiodunonitiju/Desktop/build")
                            .directory(File("/Users/abiodunonitiju/Desktop"))
                            .start()

                        println(process)

                        frontendNote = "Frontend Server Started"
                    } catch (ex: Exception){
                        frontendNote = "An error occurred on frontend server: ${ex.message}"
                    }
                } else {
                    serverProcess?.destroy()
                    serverProcess = null
                    frontendNote = "Frontend Server Stopped"
                }
            }){
                Text(frontendNote)
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
