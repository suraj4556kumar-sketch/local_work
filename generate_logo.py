import os
from PIL import Image, ImageDraw, ImageFont

# Sizes for standard launcher icons
mipmaps = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192
}

bg_color = "#F97316"  # The primary orange used in the app
text_color = "white"
res_dir = r"app\src\main\res"

def get_font(size):
    try:
        # Try Arial Bold on Windows
        return ImageFont.truetype(r"C:\Windows\Fonts\arialbd.ttf", size)
    except:
        try:
            return ImageFont.truetype(r"C:\Windows\Fonts\seguiemj.ttf", size)
        except:
            return ImageFont.load_default()

def draw_icon(size, shape="square"):
    img = Image.new("RGBA", (size, size), (255, 255, 255, 0))
    draw = ImageDraw.Draw(img)
    
    if shape == "square":
        # Draw rounded rectangle
        rad = size // 12
        draw.rounded_rectangle([0, 0, size, size], radius=rad, fill=bg_color)
    elif shape == "circle":
        draw.ellipse([0, 0, size, size], fill=bg_color)
    elif shape == "transparent":
        # Just text, no background
        pass

    font = get_font(int(size * 0.5))
    text = "LC"
    
    # Calculate text position to center it
    try:
        left, top, right, bottom = draw.textbbox((0, 0), text, font=font)
        text_w = right - left
        text_h = bottom - top
        x = (size - text_w) / 2 - left
        y = (size - text_h) / 2 - top
    except AttributeError:
        # Fallback for older Pillow versions
        text_w, text_h = draw.textsize(text, font=font)
        x = (size - text_w) / 2
        y = (size - text_h) / 2
        
    draw.text((x, y), text, fill=text_color, font=font)
    return img

def main():
    # 1. Generate legacy mipmap icons (ic_launcher.png and ic_launcher_round.png)
    for density, size in mipmaps.items():
        folder = os.path.join(res_dir, f"mipmap-{density}")
        os.makedirs(folder, exist_ok=True)
        
        sq_img = draw_icon(size, "square")
        sq_img.save(os.path.join(folder, "ic_launcher.png"))
        
        circ_img = draw_icon(size, "circle")
        circ_img.save(os.path.join(folder, "ic_launcher_round.png"))
        
    # 2. Generate Adaptive Icon Foreground (transparent background, size 432x432 for xxxhdpi)
    fg_folder = os.path.join(res_dir, "drawable-xxxhdpi")
    os.makedirs(fg_folder, exist_ok=True)
    fg_img = draw_icon(432, "transparent")
    fg_img.save(os.path.join(fg_folder, "ic_launcher_foreground.png"))

    # 3. Create Adaptive Icon XML definitions
    anydpi_folder = os.path.join(res_dir, "mipmap-anydpi-v26")
    os.makedirs(anydpi_folder, exist_ok=True)
    
    xml_content = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
"""
    with open(os.path.join(anydpi_folder, "ic_launcher.xml"), "w") as f:
        f.write(xml_content)
    with open(os.path.join(anydpi_folder, "ic_launcher_round.xml"), "w") as f:
        f.write(xml_content)

    # 4. Create Background Color
    values_folder = os.path.join(res_dir, "values")
    os.makedirs(values_folder, exist_ok=True)
    
    # We will just write a new colors.xml specifically for this or append if it's too complex. 
    # The safest way is to create a drawable XML for the background.
    drawable_folder = os.path.join(res_dir, "drawable")
    os.makedirs(drawable_folder, exist_ok=True)
    
    bg_xml = f"""<?xml version="1.0" encoding="utf-8"?>
<color xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="{bg_color}" />
"""
    with open(os.path.join(drawable_folder, "ic_launcher_background.xml"), "w") as f:
        f.write(bg_xml)

    print("Successfully generated all logo files!")

if __name__ == "__main__":
    main()
