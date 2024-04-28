import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


fun main() = application {
    Window(
        title = "Server Settings",
        icon = painterResource("logo12.jpg"),
        onCloseRequest = ::exitApplication) {
        App()
    }
}


@Composable
@Preview
fun App() {
    var backendNote = remember { mutableStateOf("Start Server") }
    var frontendNote = remember { mutableStateOf("Start client") }
    var process = remember { mutableStateOf<Process?>(null) }
    var serverProcess = remember { mutableStateOf<Process?>(null) }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Blue)
        ) {
            val backgroundImage: Painter = painterResource("desktop_bg.jpg")
            Image(
                painter = backgroundImage,
                contentDescription = null, // Content description is not needed for background image
                modifier = Modifier.fillMaxSize()
                    .matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ServerButton(
                    text = backendNote.value,
                    onClick = {
                        if (process.value == null) {
                            startBackendServer(backendNote, process)
                        } else {
                            stopProcess(process, backendNote, true)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ServerButton(
                    text = frontendNote.value,
                    onClick = {
                        if (serverProcess.value == null) {
                            startFrontendServer(frontendNote, serverProcess)
                        } else {
                            stopProcess(serverProcess, frontendNote, false)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ServerButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.widthIn(min = 200.dp)
    ) {
        Text(text)
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun startBackendServer(backendNote: MutableState<String>, process: MutableState<Process?>) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            process.value = Runtime.getRuntime().exec("java -jar /Users/abiodunonitiju/Desktop/Projects/HibernateDemo/SprintBootJspDemo/target/SprintBootJspDemo-0.0.1-SNAPSHOT.war")
            println("Backend server started with PID: ${process.value!!.pid()}")
            backendNote.value = "Server running with process id: ${process.value!!.pid()}..."
            println("Backend started....")
            println(process.value)
        } catch (ex: Exception) {
            println("Error starting backend server: ${ex.message}")
            backendNote.value = "Error starting backend server: ${ex.message}"
            // Handle error or update UI state
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun startFrontendServer(frontendNote: MutableState<String>, serverProcess: MutableState<Process?>) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
             serverProcess.value = ProcessBuilder()
                .command("serve", "-s", "/Users/abiodunonitiju/Desktop/build")
                .directory(File("/Users/abiodunonitiju"))
                .start()

            frontendNote.value = "Frontend Server is running with id: ${serverProcess.value!!.pid()}... Click again to stop it"
            println("Frontend server started with PID: ${serverProcess.value!!.pid()}")
            // Update UI state if necessary
        } catch (ex: Exception) {
            println("Error starting frontend server: ${ex.message}")
            frontendNote.value = "Error starting frontend server: ${ex.message}"
            // Handle error or update UI state
        }
    }
}

fun stopProcess(process: MutableState<Process?>, note: MutableState<String>, isBackend: Boolean) {
    process.value!!.destroy()
    println("Process stopped")
    process.value = null
    if (isBackend){
        note.value = "Server Stopped, Click to restart."
    } else{
        note.value = "Client Stopped, Click to restart."
    }
}



