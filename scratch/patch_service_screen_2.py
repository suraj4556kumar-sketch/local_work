import os

file_path = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostServiceScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Clean up hasFeesPerKm
content = content.replace('pricingType = if (hasFeesPerKm) "Visiting + Per km" else "Visiting Fees",', 'pricingType = "Visiting Fees",')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated PostServiceScreen.kt")
