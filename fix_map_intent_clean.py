import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_block = """                                      val uri = android.net.Uri.parse("geo:0,0?q=" + android.net.Uri.encode(mapAddress))
                                      val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                      intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                      intent.setPackage("com.google.android.apps.maps")
                                      try {
                                          context.startActivity(intent)
                                      } catch (e: Exception) {
                                          android.util.Log.e("MapFallback", "Native map failed", e)
                                          intent.setPackage(null)
                                          try {
                                              context.startActivity(intent)
                                          } catch (e2: Exception) {
                                              android.util.Log.e("MapFallback", "Generic map failed", e2)
                                              try {
                                                  val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(mapAddress))
                                                  val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                                  webIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                                  context.startActivity(webIntent)
                                              } catch (e3: Exception) {
                                                  android.util.Log.e("MapFallback", "Web map failed", e3)
                                                  android.widget.Toast.makeText(context, "Error: \: \", android.widget.Toast.LENGTH_LONG).show()
                                              }
                                          }
                                      }"""

new_block = """                                      val encodedLocation = java.net.URLEncoder.encode(mapAddress, "UTF-8")
                                      val geoUri = android.net.Uri.parse("geo:0,0?q=\")
                                      val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, geoUri)
                                      
                                      try {
                                          context.startActivity(mapIntent)
                                      } catch (e: android.content.ActivityNotFoundException) {
                                          try {
                                              val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=\")
                                              val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                              context.startActivity(webIntent)
                                          } catch (innerException: android.content.ActivityNotFoundException) {
                                              android.widget.Toast.makeText(context, "No application found to open maps.", android.widget.Toast.LENGTH_SHORT).show()
                                          }
                                      }"""

content = content.replace(old_block, new_block)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated Map intent to match user guide")
