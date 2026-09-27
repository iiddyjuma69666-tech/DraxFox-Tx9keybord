package com.draxfox.tx9keyboard;
import java.util.*;

/** Suggestion bar + safe auto-correct. Each map entry's key is either a common
 *  typo ("helo" -> hello) or a correct word itself (mapped to variants of itself),
 *  so the same table powers both "fix my typo" and "complete what I'm typing". */
public final class SuggestionEngine {
    private static final Map<String,String[]> SW = new HashMap<>();
    private static final Map<String,String[]> EN = new HashMap<>();

    static {
        // Typo fixes (Swahili)
        SW.put("mim", new String[]{"mimi","mimi ni","mimi mwenyewe"});
        SW.put("habar", new String[]{"habari","habari yako","habari za asubuhi"});
        SW.put("asnte", new String[]{"asante","asante sana","ahsante"});
        SW.put("tfdhali", new String[]{"tafadhali","tafadhali sana","tafadhali kidogo"});
        SW.put("karbu", new String[]{"karibu","karibu sana","karibu tena"});
        SW.put("kiinglish", new String[]{"Kiingereza","English","Kiingereza sanifu"});
        SW.put("nzr", new String[]{"nzuri","nzuri sana","mzuri"});
        // Common words (Swahili) — enables prefix-based autocomplete
        SW.put("ninapenda", new String[]{"ninapenda","napenda","ninapendelea"});
        SW.put("kujifunza", new String[]{"kujifunza","nakujifunza","kujisomea"});
        SW.put("nataka", new String[]{"nataka","ninataka","nilitaka"});
        SW.put("naomba", new String[]{"naomba","naomba msaada","naomba ruhusa"});
        SW.put("karibu", new String[]{"karibu","karibu sana","karibu tena"});
        SW.put("asante", new String[]{"asante","asante sana","asanteni"});
        SW.put("tafadhali", new String[]{"tafadhali","tafadhali sana","tafadhali kidogo"});
        SW.put("samahani", new String[]{"samahani","samahani sana","samahani kwa"});
        SW.put("kesho", new String[]{"kesho","kesho asubuhi","kesho jioni"});
        SW.put("leo", new String[]{"leo","leo asubuhi","leo jioni"});
        SW.put("sasa", new String[]{"sasa","sasa hivi","sasa basi"});
        SW.put("nyumbani", new String[]{"nyumbani","nyumbani kwangu","nyumbani kwetu"});
        SW.put("shuleni", new String[]{"shuleni","shuleni kwangu","shule"});
        SW.put("kazini", new String[]{"kazini","kazi","kazini kwangu"});
        SW.put("chakula", new String[]{"chakula","chakula kizuri","chakula cha"});
        SW.put("rafiki", new String[]{"rafiki","rafiki yangu","marafiki"});
        SW.put("familia", new String[]{"familia","familia yangu","ndugu"});
        SW.put("upendo", new String[]{"upendo","mapenzi","penzi"});
        SW.put("furaha", new String[]{"furaha","furaha sana","kufurahi"});
        SW.put("nimechoka", new String[]{"nimechoka","nimechoka sana","uchovu"});
        SW.put("njaa", new String[]{"njaa","nina njaa","njaa sana"});

        // Typo fixes (English)
        EN.put("helo", new String[]{"hello","help","held"});
        EN.put("thnks", new String[]{"thanks","thank you","think"});
        EN.put("englis", new String[]{"English","Englishes","English language"});
        EN.put("becaus", new String[]{"because","became","becoming"});
        EN.put("wnat", new String[]{"want","wants","wanted"});
        EN.put("frend", new String[]{"friend","friends","friendly"});
        // Common words (English)
        EN.put("hello", new String[]{"hello","hello there","hello again"});
        EN.put("thanks", new String[]{"thanks","thank you","thanks a lot"});
        EN.put("welcome", new String[]{"welcome","you're welcome","welcome back"});
        EN.put("please", new String[]{"please","please help","please wait"});
        EN.put("sorry", new String[]{"sorry","sorry about that","so sorry"});
        EN.put("today", new String[]{"today","today morning","today evening"});
        EN.put("tomorrow", new String[]{"tomorrow","tomorrow morning","tomorrow evening"});
        EN.put("because", new String[]{"because","because of","because I"});
        EN.put("friend", new String[]{"friend","my friend","friends"});
        EN.put("family", new String[]{"family","my family","families"});
        EN.put("love", new String[]{"love","I love","loving"});
        EN.put("happy", new String[]{"happy","happy now","happiness"});
        EN.put("tired", new String[]{"tired","so tired","tired out"});
        EN.put("hungry", new String[]{"hungry","so hungry","hungry now"});
        EN.put("learning", new String[]{"learning","learning fast","keep learning"});
    }

    public List<String> suggest(String word, String lang){
        if (word == null) return Collections.emptyList();
        String w = word.toLowerCase(Locale.ROOT);
        Map<String,String[]> m = "en".equals(lang) ? EN : SW;
        if (w.isEmpty()) return Collections.emptyList();
        if (m.containsKey(w)) return Arrays.asList(m.get(w));
        List<String> out = new ArrayList<>();
        for (String k : m.keySet()) if (k.startsWith(w)) { out.add(k); if (out.size() == 3) break; }
        if (out.isEmpty()) for (String k : m.keySet()) if (w.startsWith(k)) { out.addAll(Arrays.asList(m.get(k))); break; }
        if (out.size() > 3) return out.subList(0, 3);
        return out;
    }

    /** Used by Auto-replace: only fires on an exact known-typo match (never a fuzzy guess),
     *  so it never corrupts an intentional word. */
    public String autoCorrect(String word, String lang){
        if (word == null || word.trim().isEmpty()) return null;
        String w = word.toLowerCase(Locale.ROOT);
        Map<String,String[]> m = "en".equals(lang) ? EN : SW;
        String[] hit = m.get(w);
        if (hit == null || hit.length == 0) return null;
        String fix = hit[0];
        return fix.equalsIgnoreCase(w) ? null : fix;
    }
}
