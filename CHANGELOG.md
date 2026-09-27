# DraxFox TX9 Keyboard — Changelog

## v2.5.0
- Added: optional Theme Wallpaper backgrounds (5 original gradient images, one per theme —
  generated locally, ~5KB each, no external download). Toggle in Settings.
- Note on emoji: emoji were already full-color (Android renders Unicode emoji using the
  system's built-in color emoji font) — no image assets needed or added.

## v2.4.0
- Added: real keypress sound (KeypressFeedback + wav files), toggle in Settings.
- Added: press/release scale animation on every key, and a smooth fade-in when
  Emoji/Clipboard panels open (MotionEffects — no external library).
- Reviewed a third-party "v2.4.0-advanced-source" bundle: adopted the sound/animation
  code, rejected its bundled Swahili-English dictionary (auto-generated nonsense words,
  e.g. "habaria", "goodwa") and its 4.5MB of theme preview images (unnecessary bloat).

## v2.2.0
- Added: One-hand mode (Normal / Left / Right) via toolbar 🤚 button and Settings.
- Added: 5 themes (Aqua, Light, Carbon, Forest, Sunset), fully applied to keyboard + panels.
- Added: Persistent Sticker Library (🖼️ tab) — picked stickers are remembered for reuse.
- Added: Haptic feedback on key press (toggle in Settings).

## v2.1.0
- Added: Clipboard History (long-press 📋) with delete / clear-all, privacy toggle in Settings.
- Added: Emoji categories (Recent / Smileys / People / Nature / Symbols) instead of one long list.
- Expanded: Translator and Suggestion dictionaries (~30 more words each).

## v2.0.1 — Stability
- Added: full lifecycle safety (onStartInputView/onFinishInputView/onConfigurationChanged),
  crash guards around every user action, password-field detection (no suggestions/auto-replace
  inside password fields).
- Fixed: duplicate AppTheme resource, invalid ClipDescription.setMimeType() call.
- Added: fixed debug-signing keystore so every CI build has the same signature (no more
  "App not installed" after re-installing an update).

## v2.0.0
- Initial Advanced build: translator, suggestions, emoji, stickers.
