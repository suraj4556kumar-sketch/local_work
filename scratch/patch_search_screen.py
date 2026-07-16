import re

filepath = r"app/src/main/java/com/example/localconnect/ServiceSearchScreen.kt"
with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

# Add TabRow import if missing
if "import androidx.compose.material3.Tab" not in content:
    content = content.replace("import androidx.compose.material3.*", "import androidx.compose.material3.*\nimport androidx.compose.material3.Tab\nimport androidx.compose.material3.TabRow\nimport androidx.compose.material3.TabRowDefaults")

old_start = """    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Search & Filters Header"""

new_start = """    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Tab Row Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = OrangePrimary,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.White
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.find_services), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Bookings", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.7f)
            )
        }

        if (selectedTab == 0) {
            // Search & Filters Header"""

content = content.replace(old_start, new_start)

old_end = """        }
    }
}
"""

new_end = """        }
        } else {
            MyServiceBookingsScreen(session = session)
        }
    }
}
"""

# Replace the last occurrence of old_end
content = new_end.join(content.rsplit(old_end, 1))

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated ServiceSearchScreen.kt")
