import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath

@Composable
@Preview
fun App(
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    MaterialTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = value,
                    onCheckedChange = onValueChange
                )
                Text("Check the box to trigger the issue")
            }
        }

    }
}

val prefKey = booleanPreferencesKey("PREF_VALUE")

fun main() {
    val datastore = PreferenceDataStoreFactory.createWithPath{ "datastore_demo.preferences_pb".toPath() }

    application {
        val scope = rememberCoroutineScope()

        Window(onCloseRequest = ::exitApplication) {
            val value by datastore.data.map { it[prefKey] ?: false }.collectAsState(false)

            App(
                value = value,
                onValueChange = { newValue ->
                    scope.launch {
                        datastore.updateData {
                            it.toMutablePreferences().apply {
                                set(prefKey, newValue)
                            }
                        }
                    }
                }
            )
        }
    }
}
