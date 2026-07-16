import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_block = """                                      try {
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

new_block = """                                      try {
                                          mapIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                          context.startActivity(mapIntent)
                                      } catch (e: Exception) {
                                          android.util.Log.e("MapError", "First intent failed", e)
                                          try {
                                              val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=\")
                                              val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                              webIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                              context.startActivity(webIntent)
                                          } catch (innerException: Exception) {
                                              android.util.Log.e("MapError", "Second intent failed", innerException)
                                              android.widget.Toast.makeText(context, "Error: \", android.widget.Toast.LENGTH_SHORT).show()
                                          }
                                      }"""

content = content.replace(old_block, new_block)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated Map intent to log exceptions and use flags")
