import os
import re

strings_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/res/values/strings.xml'
post_job_file = 'c:/Users/dhani/AndroidStudioProjects/LocalWork/app/src/main/java/com/example/localconnect/PostJobScreen.kt'

# 1. Update strings.xml
with open(strings_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Change Employer/Company Name * to Company Name *
content = content.replace(
    '<string name="employer_company_star">Employer / Company Name *</string>',
    '<string name="employer_company_star">Company Name *</string>'
)

# Add * to mandatory fields
updates = [
    ('<string name="monthly_salary">Monthly Salary (₹)</string>', '<string name="monthly_salary">Monthly Salary (₹) *</string>'),
    ('<string name="work_address_building">Work Address / Building</string>', '<string name="work_address_building">Work Address / Building *</string>'),
    ('<string name="state_label">State</string>', '<string name="state_label">State *</string>'),
    ('<string name="exp_required">Experience Required</string>', '<string name="exp_required">Experience Required *</string>'),
    ('<string name="edu_req">Education Required</string>', '<string name="edu_req">Education Required *</string>'),
    ('<string name="gender_pref">Gender Preference</string>', '<string name="gender_pref">Gender Preference *</string>'),
    ('<string name="desc_job_role_env">Describe the job role, environment...</string>', '<string name="desc_job_role_env">Describe the job role, environment... *</string>')
]

for old, new in updates:
    content = content.replace(old, new)

with open(strings_file, 'w', encoding='utf-8') as f:
    f.write(content)


# 2. Update PostJobScreen.kt to decrease font size of dynamic texts
with open(post_job_file, 'r', encoding='utf-8') as f:
    kt_content = f.read()

# Replace OutlinedTextField( to OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
# We have to be careful with formatting. Let's use regex to find OutlinedTextField and add textStyle.
# OutlinedTextField( -> OutlinedTextField(\n                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
kt_content = re.sub(
    r'OutlinedTextField\(',
    r'OutlinedTextField(textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),',
    kt_content
)

with open(post_job_file, 'w', encoding='utf-8') as f:
    f.write(kt_content)

print("Patch applied successfully.")
