# FocusFlow Website Assets

This folder contains the website assets.

## Image Requirements

For the website to display properly, add these screenshots from your Android device:

1. `phone-main.png` - Main screen screenshot (portrait, ~9:19 aspect ratio)
2. `screenshot-main.png` - Main dashboard screenshot
3. `screenshot-timer.png` - Timer dialog screenshot
4. `screenshot-stats.png` - Statistics screen screenshot
5. `screenshot-weekly.png` - Weekly report screenshot
6. `screenshot-settings.png` - Settings screen screenshot
7. `screenshot-addtask.png` - Add task dialog screenshot
8. `og-image.png` - Open Graph image (1200x630px recommended)

## Taking Screenshots

Use ADB to capture screenshots from your device:
```bash
adb exec-out screencap -p > screenshot.png
```

Then copy to this folder:
```bash
adb pull /sdcard/screenshot.png ./images/screenshot.png
```