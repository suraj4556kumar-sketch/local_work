import os

job_app_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/JobApplicationScreen.kt'
with open(job_app_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Add asterisks to labels
replacements = {
    'Text(stringResource(R.string.full_name), fontSize = 14.sp, fontWeight = FontWeight.Medium':
        'Text(stringResource(R.string.full_name) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium',
        
    'Text(stringResource(R.string.phone_number), fontSize = 14.sp, fontWeight = FontWeight.Medium':
        'Text(stringResource(R.string.phone_number) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium',
        
    'Text(stringResource(R.string.age), fontSize = 14.sp, fontWeight = FontWeight.Medium':
        'Text(stringResource(R.string.age) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium',
        
    'Text(stringResource(R.string.gender), fontSize = 14.sp, fontWeight = FontWeight.Medium':
        'Text(stringResource(R.string.gender) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium',
        
    'Text(stringResource(R.string.work_exp), fontSize = 18.sp, fontWeight = FontWeight.Bold':
        'Text(stringResource(R.string.work_exp) + " *", fontSize = 18.sp, fontWeight = FontWeight.Bold',
        
    'Text(stringResource(R.string.documents), fontSize = 18.sp, fontWeight = FontWeight.Bold':
        'Text(stringResource(R.string.documents) + " *", fontSize = 18.sp, fontWeight = FontWeight.Bold',
        
    'Text(stringResource(R.string.pref_lang), fontSize = 18.sp, fontWeight = FontWeight.Bold':
        'Text(stringResource(R.string.pref_lang) + " *", fontSize = 18.sp, fontWeight = FontWeight.Bold'
}

for old_str, new_str in replacements.items():
    content = content.replace(old_str, new_str)

# Add validation logic to Submit button
validation_code = """                                if (fullName.isBlank() || phone.isBlank() || age.isBlank()) {
                                    android.widget.Toast.makeText(context.applicationContext, "Please fill all mandatory fields (*)", android.widget.Toast.LENGTH_SHORT).show()
                                    isSubmitting = false
                                    return@launch
                                }
                                
                                if (docUris.size < requiredDocs.size) {"""

content = content.replace('                                if (docUris.size < requiredDocs.size) {', validation_code)

with open(job_app_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated JobApplicationScreen.kt")
