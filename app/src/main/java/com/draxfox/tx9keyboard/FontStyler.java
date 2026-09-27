package com.draxfox.tx9keyboard;

/** Converts plain text into real Unicode "styled" glyphs (Bold/Italic/Mono/Script).
 *  This works in ANY app since the result is genuine Unicode text, not formatting metadata. */
public final class FontStyler {
    public enum Style { NORMAL, BOLD, ITALIC, BOLD_ITALIC, MONO, SCRIPT }

    public static String apply(String text, Style style) {
        if (style == Style.NORMAL || text == null || text.isEmpty()) return text;
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) out.append(styleChar(text.charAt(i), style));
        return out.toString();
    }

    private static String styleChar(char c, Style style) {
        Character ex = exception(c, style);
        if (ex != null) return String.valueOf(ex.charValue());
        int base;
        if (c >= 'A' && c <= 'Z') { base = upperBase(style); return base < 0 ? String.valueOf(c) : new String(Character.toChars(base + (c - 'A'))); }
        if (c >= 'a' && c <= 'z') { base = lowerBase(style); return base < 0 ? String.valueOf(c) : new String(Character.toChars(base + (c - 'a'))); }
        if (c >= '0' && c <= '9') { base = digitBase(style); return base < 0 ? String.valueOf(c) : new String(Character.toChars(base + (c - '0'))); }
        return String.valueOf(c);
    }

    // Unicode has historical gaps in the Mathematical Alphanumeric block for a few
    // Script/Italic letters — those live in the older Letterlike Symbols block instead.
    private static Character exception(char c, Style s) {
        if (s == Style.ITALIC && c == 'h') return '\u210E';
        if (s == Style.SCRIPT) {
            switch (c) {
                case 'B': return '\u212C'; case 'E': return '\u2130'; case 'F': return '\u2131';
                case 'H': return '\u210B'; case 'I': return '\u2110'; case 'L': return '\u2112';
                case 'M': return '\u2133'; case 'R': return '\u211B';
                case 'e': return '\u212F'; case 'g': return '\u210A'; case 'o': return '\u2134';
            }
        }
        return null;
    }

    private static int upperBase(Style s) { switch (s) { case BOLD: return 0x1D400; case ITALIC: return 0x1D434; case BOLD_ITALIC: return 0x1D468; case MONO: return 0x1D670; case SCRIPT: return 0x1D49C; default: return -1; } }
    private static int lowerBase(Style s) { switch (s) { case BOLD: return 0x1D41A; case ITALIC: return 0x1D44E; case BOLD_ITALIC: return 0x1D482; case MONO: return 0x1D68A; case SCRIPT: return 0x1D4B6; default: return -1; } }
    private static int digitBase(Style s) { switch (s) { case BOLD: return 0x1D7CE; case MONO: return 0x1D7F6; default: return -1; } }

    public static Style next(Style s) { Style[] v = Style.values(); return v[(s.ordinal() + 1) % v.length]; }
    public static String shortLabel(Style s) { switch (s) { case BOLD: return "𝐁"; case ITALIC: return "𝑰"; case BOLD_ITALIC: return "𝑩𝑰"; case MONO: return "𝙼"; case SCRIPT: return "𝓢"; default: return "Aa"; } }
    public static String fullLabel(Style s) { switch (s) { case BOLD: return "Bold"; case ITALIC: return "Italic"; case BOLD_ITALIC: return "Bold Italic"; case MONO: return "Monospace"; case SCRIPT: return "Script"; default: return "Normal"; } }
}
