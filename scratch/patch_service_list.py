import re

filepath = r"app/src/main/java/com/example/localconnect/ServiceRequestListScreen.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

placeholder = """                            // Placeholder for Picture of Problem
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Image, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("No picture attached", color = Color.Gray, fontSize = 12.sp)
                                }
                            }"""

new_code = """                            if (request.imageUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("ATTACHED PICTURE:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                Spacer(modifier = Modifier.height(8.dp))
                                coil.compose.AsyncImage(
                                    model = request.imageUrl,
                                    contentDescription = "Problem Picture",
                                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                // Placeholder for Picture of Problem
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.Image, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("No picture attached", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }"""

content = content.replace(placeholder, new_code)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated ServiceRequestListScreen.kt")
