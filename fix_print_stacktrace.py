import os

file_path = 'app/src/main/java/com/example/localconnect/JobDetailsScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_catch = """                                              } catch (e3: Exception) {
                                                  android.widget.Toast.makeText(context, "Error: \", android.widget.Toast.LENGTH_LONG).show()
                                              }"""

new_catch = """                                              } catch (e3: Exception) {
                                                  e3.printStackTrace()
                                                  android.widget.Toast.makeText(context, "Error: \", android.widget.Toast.LENGTH_LONG).show()
                                              }"""

content = content.replace(old_catch, new_catch)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated catch block to print stack trace")
