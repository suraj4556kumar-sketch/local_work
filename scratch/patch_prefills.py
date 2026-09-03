import os

kt_path = "app/src/main/java/com/example/localconnect/PostJobScreen.kt"
with open(kt_path, 'r', encoding='utf-8') as f:
    kt_content = f.read()

# Replace experienceRequired
old_exp = "var experienceRequired by remember { mutableStateOf(androidx.compose.ui.res.stringResource(R.string.exp_1_3)) }"
new_exp = """val initialExp = androidx.compose.ui.res.stringResource(R.string.exp_1_3)
    var experienceRequired by remember(initialExp) { mutableStateOf(initialExp) }"""
kt_content = kt_content.replace(old_exp, new_exp)

# Replace educationRequired
old_edu = "var educationRequired by remember { mutableStateOf(androidx.compose.ui.res.stringResource(R.string.edu_12th)) }"
new_edu = """val initialEdu = androidx.compose.ui.res.stringResource(R.string.edu_12th)
    var educationRequired by remember(initialEdu) { mutableStateOf(initialEdu) }"""
kt_content = kt_content.replace(old_edu, new_edu)

# Replace genderPreference
old_gender = "var genderPreference by remember { mutableStateOf(androidx.compose.ui.res.stringResource(R.string.gender_male)) }"
new_gender = """val initialGender = androidx.compose.ui.res.stringResource(R.string.gender_male)
    var genderPreference by remember(initialGender) { mutableStateOf(initialGender) }"""
kt_content = kt_content.replace(old_gender, new_gender)

with open(kt_path, 'w', encoding='utf-8') as f:
    f.write(kt_content)
print("Updated prefilled state declarations in PostJobScreen.kt")
