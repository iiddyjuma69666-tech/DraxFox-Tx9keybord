package com.draxfox.tx9keyboard;
import java.util.*;

/** Offline word-by-word Swahili<->English translator. Punctuation at the end of a
 *  word (. , ! ?) is preserved. Unknown words are left untouched rather than guessed. */
public final class TranslatorEngine {
    public interface Callback { void done(String result); void error(String message); }

    private static final Map<String,String> SW_EN = new HashMap<>();
    private static final Map<String,String> EN_SW = new HashMap<>();

    private static void add(String sw, String en){
        SW_EN.put(sw, en);
        String k = en.toLowerCase(Locale.ROOT);
        if (!EN_SW.containsKey(k)) EN_SW.put(k, sw);
    }

    static {
        add("mimi","I"); add("wewe","you"); add("yeye","he"); add("sisi","we"); add("ninyi","you"); add("wao","they");
        add("ninapenda","I love"); add("napenda","I like"); add("nataka","I want"); add("ninataka","I want"); add("naomba","please");
        add("nilikuwa","I was"); add("nitakuwa","I will be"); add("nimefika","I have arrived"); add("naenda","I am going");
        add("kujifunza","learning"); add("kusoma","reading"); add("kuandika","writing"); add("kuongea","speaking"); add("kusikiliza","listening");
        add("kuona","seeing"); add("kula","eating"); add("kunywa","drinking"); add("kulala","sleeping"); add("kucheza","playing");
        add("kiingereza","English"); add("kiswahili","Swahili"); add("lugha","language"); add("maneno","words"); add("sentensi","sentence");
        add("habari","hello"); add("asante","thanks"); add("karibu","welcome"); add("samahani","sorry"); add("pole","sorry"); add("tafadhali","please");
        add("kwaheri","goodbye"); add("usiku","night"); add("mchana","afternoon"); add("asubuhi","morning");
        add("ndiyo","yes"); add("hapana","no"); add("labda","maybe"); add("sawa","okay"); add("vizuri","well"); add("mbaya","bad");
        add("leo","today"); add("kesho","tomorrow"); add("jana","yesterday"); add("sasa","now"); add("baadaye","later"); add("mapema","early");
        add("nyumbani","home"); add("shuleni","school"); add("kazini","work"); add("mjini","town"); add("safari","trip"); add("njiani","on the way");
        add("chakula","food"); add("maji","water"); add("pesa","money"); add("gari","car"); add("simu","phone"); add("kompyuta","computer"); add("kitabu","book");
        add("rafiki","friend"); add("familia","family"); add("mama","mother"); add("baba","father"); add("mtoto","child"); add("mume","husband"); add("mke","wife"); add("ndugu","sibling");
        add("upendo","love"); add("furaha","happiness"); add("huzuni","sadness"); add("hasira","anger"); add("amani","peace"); add("matumaini","hope");
        add("kubwa","big"); add("kidogo","small"); add("haraka","fast"); add("polepole","slowly"); add("mzuri","good"); add("refu","long"); add("fupi","short"); add("moto","hot"); add("baridi","cold");
        add("moja","one"); add("mbili","two"); add("tatu","three"); add("nne","four"); add("tano","five"); add("sita","six"); add("saba","seven"); add("nane","eight"); add("tisa","nine"); add("kumi","ten");
        add("saa","hour"); add("siku","day"); add("wiki","week"); add("mwezi","month"); add("mwaka","year"); add("dakika","minute");
        add("kazi","work"); add("shule","school"); add("chuo","college"); add("mtu","person"); add("watu","people"); add("daktari","doctor"); add("mwalimu","teacher");
        add("njaa","hungry"); add("kiu","thirsty"); add("nimechoka","tired"); add("lala","sleep"); add("amka","wake up"); add("nenda","go"); add("njoo","come"); add("simama","stop"); add("kaa","sit"); add("ondoka","leave");
        add("mzigo","load"); add("barua","letter"); add("picha","picture"); add("sauti","sound"); add("muziki","music"); add("filamu","movie"); add("mchezo","game"); add("mpira","ball");
    }

    public static String offline(String input, String from, String to){
        if (input == null || input.trim().isEmpty()) return "";
        String s = input.trim();
        if (from.equals("sw") && to.equals("en")) return words(s, SW_EN);
        if (from.equals("en") && to.equals("sw")) return words(s, EN_SW);
        return s;
    }

    private static String words(String s, Map<String,String> m){
        StringBuilder b = new StringBuilder();
        for (String w : s.split("(\\s+)", -1)){
            String trail = "";
            String core = w;
            if (core.length() > 0){
                char last = core.charAt(core.length() - 1);
                if (last == '.' || last == ',' || last == '!' || last == '?'){
                    trail = String.valueOf(last);
                    core = core.substring(0, core.length() - 1);
                }
            }
            String k = core.toLowerCase(Locale.ROOT);
            String v = m.get(k);
            b.append(v == null ? w : (v + trail)).append(' ');
        }
        return b.toString().trim();
    }
}
