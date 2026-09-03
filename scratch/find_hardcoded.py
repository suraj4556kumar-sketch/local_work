import os
import re

def find_hardcoded_texts(directory):
    hardcoded = []
    text_pattern = re.compile(r'(?:Text|TranslatedText)\(\s*"([^"]+)"')
    
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith('.kt') and "DummyData" not in file:
                filepath = os.path.join(root, file)
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                    matches = text_pattern.findall(content)
                    for match in matches:
                        # Only keep strings with letters
                        if any(c.isalpha() for c in match) and "http" not in match:
                            hardcoded.append((filepath, match))
    return hardcoded

if __name__ == '__main__':
    results = find_hardcoded_texts('app/src/main/java/com/example/localconnect')
    
    with open('scratch/hardcoded_results.txt', 'w', encoding='utf-8') as f:
        f.write(f"Total hardcoded static strings: {len(results)}\n")
        for path, match in results:
            f.write(f"{os.path.basename(path)}: {match}\n")
    
    print(f"Total hardcoded static strings: {len(results)}")
