import re

filepath = "app/src/main/java/com/prefeitura/cacaplaca/feature/map/ui/screens/MapScreen.kt"
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace("var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }", "var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }\n    var showUpdateDialog by remember { mutableStateOf(false) }")
# We already added the bottom code correctly, but maybe it has syntax errors since the variables were out of scope. Let's see if the build errors go away.

with open(filepath, 'w') as f:
    f.write(content)

