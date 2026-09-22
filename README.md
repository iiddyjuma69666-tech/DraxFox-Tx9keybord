# DraxFox TX9 Keyboard — Advanced v2

Package: `com.draxfox.tx9keyboard`

A clean Android IME rebuild focused on a real typing experience.

## New in Advanced v2
- Large emoji library with quick emoji insertion.
- Dedicated emoji/sticker mode inside the keyboard.
- **IMAGE STICKER** picker using Android document picker.
- Real Android rich-content insertion through `InputConnection.commitContent()` where the receiving app supports images.
- Safe clipboard fallback for apps that do not accept image content.
- Sticker shortcut in the suggestion row.
- Clipboard paste shortcut.
- Language toggle Swahili/English.
- Suggestion bar remains user-controlled; no silent correction by default.
- Translator toolbar shortcut using the existing Swahili ↔ English engine architecture.
- Modern dark/aqua keyboard foundation.

## Sticker behavior
The keyboard does not fake an image as text. It selects a real image URI and attempts Android's content-insertion API. Some receiving apps (especially apps/editors that do not advertise image MIME types) may reject rich content; TX9 then puts the selected image in the clipboard so it can be pasted manually.

## Build
Open in Android Studio and run `assembleDebug`. The APK is generated at `app/build/outputs/apk/debug/`.
