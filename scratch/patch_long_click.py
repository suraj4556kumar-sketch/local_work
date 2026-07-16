import re

filepath = r"app/src/main/java/com/example/localconnect/MyServiceBookingsScreen.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

import_lines = """
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
"""

if "import androidx.compose.foundation.combinedClickable" not in content:
    content = content.replace("import androidx.compose.foundation.background", import_lines + "import androidx.compose.foundation.background")

# Screen changes
old_screen_start = """    var bookings by remember { mutableStateOf<List<ServiceRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }"""

new_screen_start = """    var bookings by remember { mutableStateOf<List<ServiceRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var requestToRemove by remember { mutableStateOf<ServiceRequest?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current"""

content = content.replace(old_screen_start, new_screen_start)

old_list = """        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookings.sortedByDescending { it.appliedOn }) { request ->
                SeekerBookingCard(request = request)
            }
        }
    }
}"""

new_list = """        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookings.sortedByDescending { it.appliedOn }) { request ->
                SeekerBookingCard(request = request, onRemove = { requestToRemove = request })
            }
        }
    }

    if (requestToRemove != null) {
        AlertDialog(
            onDismissRequest = { requestToRemove = null },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to remove this booking?") },
            confirmButton = {
                TextButton(onClick = {
                    val reqId = requestToRemove!!.id
                    requestToRemove = null
                    coroutineScope.launch {
                        val success = FirebaseRepository.deleteServiceRequest(reqId)
                        if (success) {
                            bookings = bookings.filter { it.id != reqId }
                            Toast.makeText(context, "Booking removed", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { requestToRemove = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}"""

content = content.replace(old_list, new_list)


# Card changes
old_card_sig = """@Composable
fun SeekerBookingCard(request: ServiceRequest) {"""

new_card_sig = """@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeekerBookingCard(request: ServiceRequest, onRemove: () -> Unit) {"""

content = content.replace(old_card_sig, new_card_sig)

old_modifier = """        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {"""

new_modifier = """        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().combinedClickable(
            onClick = {},
            onLongClick = onRemove
        )
    ) {"""

content = content.replace(old_modifier, new_modifier)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated MyServiceBookingsScreen.kt")
