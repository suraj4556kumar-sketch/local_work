import os

post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'
with open(post_job_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Add isAddressFromMap state
if 'var isAddressFromMap by remember { mutableStateOf(false) }' not in content:
    content = content.replace('var landmark by remember { mutableStateOf("") }',
                              'var landmark by remember { mutableStateOf("") }\n    var isAddressFromMap by remember { mutableStateOf(false) }')

# Add isAddressFromMap to LocationSelectorMap
if 'isAddressFromMap = isAddressFromMap' not in content:
    content = content.replace('addressQuery = "$workAddress $city $state $pincode",\n                    currentLat = latitude,',
                              'addressQuery = "$workAddress $city $state $pincode",\n                    isAddressFromMap = isAddressFromMap,\n                    currentLat = latitude,')

# Add isAddressFromMap = true to onLocationSelected
if 'isAddressFromMap = true' not in content:
    content = content.replace('onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->\n                        latitude = lat',
                              'onLocationSelected = { lat, lon, street, newCity, newState, pin, forceOverwrite ->\n                        isAddressFromMap = true\n                        latitude = lat')

# Add isAddressFromMap = false to textfields
content = content.replace('value = workAddress, onValueChange = { workAddress = it },',
                          'value = workAddress, onValueChange = { workAddress = it; isAddressFromMap = false },')
content = content.replace('value = city, onValueChange = { city = it },',
                          'value = city, onValueChange = { city = it; isAddressFromMap = false },')
content = content.replace('value = state, onValueChange = { state = it },',
                          'value = state, onValueChange = { state = it; isAddressFromMap = false },')
content = content.replace('value = pincode, onValueChange = { pincode = it },',
                          'value = pincode, onValueChange = { pincode = it; isAddressFromMap = false },')
content = content.replace('value = landmark, onValueChange = { landmark = it },',
                          'value = landmark, onValueChange = { landmark = it; isAddressFromMap = false },')

with open(post_job_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostJobScreen.kt")

loc_map_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/LocationSelectorMap.kt'
with open(loc_map_file, 'r', encoding='utf-8') as f:
    loc_content = f.read()

# Add isAddressFromMap param
if 'isAddressFromMap: Boolean = false,' not in loc_content:
    loc_content = loc_content.replace('addressQuery: String, \n    currentLat: Double?,',
                                      'addressQuery: String, \n    isAddressFromMap: Boolean = false,\n    currentLat: Double?,')

# Add delay import if missing
if 'import kotlinx.coroutines.delay' not in loc_content:
    loc_content = loc_content.replace('import kotlinx.coroutines.withContext',
                                      'import kotlinx.coroutines.withContext\nimport kotlinx.coroutines.delay')

# Add LaunchedEffect(addressQuery)
effect_code = """
    // React to manual address typing
    LaunchedEffect(addressQuery) {
        if (!isAddressFromMap && addressQuery.isNotBlank() && addressQuery.length > 5) {
            delay(1500) // Debounce
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(addressQuery, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val pt = LatLng(addr.latitude, addr.longitude)
                    withContext(Dispatchers.Main) {
                        markerPosition = pt
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pt, 15f))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // React to currentLat/currentLon changes"""

if '// React to manual address typing' not in loc_content:
    loc_content = loc_content.replace('// React to currentLat/currentLon changes', effect_code)

with open(loc_map_file, 'w', encoding='utf-8') as f:
    f.write(loc_content)

print("Updated LocationSelectorMap.kt")
