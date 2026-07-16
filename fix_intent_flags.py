import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_block = """                                      val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                      intent.setPackage("com.google.android.apps.maps")
                                      try {
                                          context.startActivity(intent)
                                      } catch (e: Exception) {
                                          intent.setPackage(null)
                                          try {
                                              context.startActivity(intent)
                                          } catch (e2: Exception) {
                                              try {
                                                  val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(mapAddress))
                                                  val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                                  context.startActivity(webIntent)
                                              } catch (e3: Exception) {
                                                  android.widget.Toast.makeText(context, "No app or browser found", android.widget.Toast.LENGTH_SHORT).show()
                                              }
                                          }
                                      }"""

new_block = """                                      val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                      intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                      intent.setPackage("com.google.android.apps.maps")
                                      try {
                                          context.startActivity(intent)
                                      } catch (e: Exception) {
                                          intent.setPackage(null)
                                          try {
                                              context.startActivity(intent)
                                          } catch (e2: Exception) {
                                              try {
                                                  val webUri = android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(mapAddress))
                                                  val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, webUri)
                                                  webIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                                  context.startActivity(webIntent)
                                              } catch (e3: Exception) {
                                                  android.widget.Toast.makeText(context, "No app or browser found", android.widget.Toast.LENGTH_SHORT).show()
                                              }
                                          }
                                      }"""

content = content.replace(old_block, new_block)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated intent flags")
