import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/ServiceSearchScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Add currentDayOfWeek to ServiceSearchScreen
# Locate the advancedFiltered val
target_filter = """    val advancedFiltered = sortedServices.filter {
        (if (maxDistance >= 100.0) true else it.distanceKm in 0.0..maxDistance) &&
        it.price <= maxRate &&
        it.rating >= minRating &&
        (selectedServiceFilter == stringResource(R.string.all_services) || it.category == selectedServiceFilter)
    }"""

replacement_filter = """    val currentDay = remember { java.time.LocalDate.now().dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH) }
    val advancedFiltered = sortedServices.filter {
        (if (maxDistance >= 100.0) true else it.distanceKm in 0.0..maxDistance) &&
        it.price <= maxRate &&
        it.rating >= minRating &&
        (selectedServiceFilter == stringResource(R.string.all_services) || it.category == selectedServiceFilter) &&
        (it.availableDays.isEmpty() || it.availableDays.contains(currentDay))
    }"""

content = content.replace(target_filter, replacement_filter)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated ServiceSearchScreen.kt")
