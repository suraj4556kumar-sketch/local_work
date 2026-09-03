import os

post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'
with open(post_job_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Add isPinSaved state
if 'var isPinSaved by remember { mutableStateOf(false) }' not in content:
    content = content.replace('var isAddressFromMap by remember { mutableStateOf(false) }',
                              'var isAddressFromMap by remember { mutableStateOf(false) }\n    var isPinSaved by remember { mutableStateOf(false) }')

# In Use Current Location
content = content.replace('latitude = location.latitude\n                                                    longitude = location.longitude',
                          'latitude = location.latitude\n                                                    longitude = location.longitude\n                                                    isPinSaved = false')

# In manual typing textfields
content = content.replace('value = workAddress, onValueChange = { workAddress = it; isAddressFromMap = false },',
                          'value = workAddress, onValueChange = { workAddress = it; isAddressFromMap = false; isPinSaved = false },')
content = content.replace('value = city, onValueChange = { city = it; isAddressFromMap = false },',
                          'value = city, onValueChange = { city = it; isAddressFromMap = false; isPinSaved = false },')
content = content.replace('value = state, onValueChange = { state = it; isAddressFromMap = false },',
                          'value = state, onValueChange = { state = it; isAddressFromMap = false; isPinSaved = false },')
content = content.replace('value = pincode, onValueChange = { pincode = it; isAddressFromMap = false },',
                          'value = pincode, onValueChange = { pincode = it; isAddressFromMap = false; isPinSaved = false },')
content = content.replace('value = landmark, onValueChange = { landmark = it; isAddressFromMap = false },',
                          'value = landmark, onValueChange = { landmark = it; isAddressFromMap = false; isPinSaved = false },')

# In LocationSelectorMap onLocationSelected
content = content.replace('if (forceOverwrite) {\n                            if (street.isNotBlank()) workAddress = street',
                          'if (forceOverwrite) {\n                            isPinSaved = true\n                            if (street.isNotBlank()) workAddress = street')
content = content.replace('} else {\n                            if (workAddress.isBlank()) workAddress = street',
                          '} else {\n                            isPinSaved = false\n                            if (workAddress.isBlank()) workAddress = street')


# In Post Job Button validation
validation_block = """                            if (jobTitle.isBlank() || city.isBlank() || salaryMin.isBlank()) {
                                Toast.makeText(context.applicationContext, "Please fill required fields (Title, City, Min Salary)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isPinSaved) {
                                Toast.makeText(context.applicationContext, "Please confirm your location by clicking 'Save Pin Location'", Toast.LENGTH_SHORT).show()
                                return@Button
                            }"""
content = content.replace('                            if (jobTitle.isBlank() || city.isBlank() || salaryMin.isBlank()) {\n                                Toast.makeText(context.applicationContext, "Please fill required fields (Title, City, Min Salary)", Toast.LENGTH_SHORT).show()\n                                return@Button\n                            }', validation_block)


with open(post_job_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostJobScreen.kt")
