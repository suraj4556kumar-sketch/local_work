import re

filepath = r"app/src/main/java/com/example/localconnect/CustomerDetailsScreen.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

import_lines = """
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
"""
if "import coil.compose.AsyncImage" not in content:
    content = content.replace("import androidx.compose.ui.res.stringResource", "import androidx.compose.ui.res.stringResource\n" + import_lines)

# Find the var declarations
var_decls = """    var agreeShare by remember { mutableStateOf(false) }
    var agreePrice by remember { mutableStateOf(false) }"""

new_var_decls = """    var agreeShare by remember { mutableStateOf(false) }
    var agreePrice by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )"""

content = content.replace(var_decls, new_var_decls)

# Problem Description section
problem_desc_section = """            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(specialInstructionsStr.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    placeholder = { Text(stringResource(R.string.write_extra_inst), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )"""

new_problem_desc_section = """            Column(modifier = Modifier.background(Color.White).padding(20.dp).fillMaxWidth()) {
                Text(specialInstructionsStr.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    placeholder = { Text(stringResource(R.string.write_extra_inst), color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Upload Picture Section
                Text("ATTACH PICTURE (OPTIONAL)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF97316))
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri == null) "Upload Picture" else "Change Picture", fontWeight = FontWeight.SemiBold)
                    }
                    
                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected image",
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }"""

content = content.replace(problem_desc_section, new_problem_desc_section)


# Update booking logic
old_booking = """                        onClick = {
                            isBooking = true
                            coroutineScope.launch {
                                val request = ServiceRequest(
                                    seekerId = session.phone,
                                    providerId = service.providerId,
                                    serviceId = service.id,
                                    serviceTitle = service.serviceTitle,
                                    status = "Pending",
                                    scheduledDate = selectedDate,
                                    scheduledTime = selectedTimeSlot,
                                    problemDescription = problemDescription, // Separated out instructions
                                    appliedOn = System.currentTimeMillis(),
                                    customerName = name,
                                    customerPhone = phone,
                                    customerAltPhone = altPhone,
                                    customerAddress = address,
                                    customerLandmark = landmark,
                                    customerCity = city,
                                    latitude = latitude,
                                    longitude = longitude,
                                    instructions = instructions
                                )
                                val success = FirebaseRepository.createServiceRequest(request)"""

new_booking = """                        onClick = {
                            isBooking = true
                            coroutineScope.launch {
                                var finalImageUrl = ""
                                if (selectedImageUri != null) {
                                    val uploadedUrl = FirebaseRepository.uploadServiceImage(selectedImageUri!!)
                                    if (uploadedUrl != null) {
                                        finalImageUrl = uploadedUrl
                                    }
                                }
                                val request = ServiceRequest(
                                    seekerId = session.phone,
                                    providerId = service.providerId,
                                    serviceId = service.id,
                                    serviceTitle = service.serviceTitle,
                                    status = "Pending",
                                    scheduledDate = selectedDate,
                                    scheduledTime = selectedTimeSlot,
                                    problemDescription = problemDescription, // Separated out instructions
                                    appliedOn = System.currentTimeMillis(),
                                    customerName = name,
                                    customerPhone = phone,
                                    customerAltPhone = altPhone,
                                    customerAddress = address,
                                    customerLandmark = landmark,
                                    customerCity = city,
                                    latitude = latitude,
                                    longitude = longitude,
                                    instructions = instructions,
                                    imageUrl = finalImageUrl
                                )
                                val success = FirebaseRepository.createServiceRequest(request)"""

content = content.replace(old_booking, new_booking)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated CustomerDetailsScreen.kt")
