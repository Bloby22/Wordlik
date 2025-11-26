package org.bloby.wordlik.utils;

import java.util.*;
import java.util.stream.Collectors;

public class WordList {
    
    private final java.util.List<String> words;
    private final Set<String> wordSet;
    private final Random random;
    private final Map<String, Integer> wordDifficulty;
    private final Map<Character, java.util.List<String>> wordsByFirstLetter;
    private int totalWordsUsed;
    
    public WordList() {
        this.words = new ArrayList<>();
        this.wordSet = new HashSet<>();
        this.random = new Random();
        this.wordDifficulty = new HashMap<>();
        this.wordsByFirstLetter = new HashMap<>();
        this.totalWordsUsed = 0;
        loadWords();
        categorizeWords();
        calculateDifficulty();
    }
    
    private void loadWords() {
        words.addAll(Arrays.asList(
       "ahoj", "auto", "barva", "beran", "blesk",
            "bobek", "bouře", "brána", "bratr", "břicho",
            "bříza", "brzda", "bufet", "cesta", "cihla",
            "citát", "česko", "číslo", "dáma", "deska",
            "dílo", "divák", "dopis", "dráha", "drama",
            "dřevo", "duha", "duše", "dvěře", "džbán",
            "fazol", "film", "flétna", "forma", "fotka",
            "fraška", "gesto", "had", "hala", "halas",
            "harfa", "havran", "herec", "hlava", "hlína",
            "hluk", "hmat", "hnízdo", "hodně", "holka",
            "hora", "houba", "houska", "hovor", "hrabě",
            "hrad", "hrana", "hrnek", "hrob", "hroch",
            "hrom", "hruška", "hrůza", "hřbet", "hřích",
            "hudba", "hurá", "chata", "chlad", "chleb",
            "chlup", "chmel", "chodba", "chyba", "chytrý",
            "jabko", "jahoda", "jaro", "jasno", "jelen",
            "jetel", "ježek", "jídlo", "jih", "jméno",
            "jóga", "kabel", "kabát", "kámen", "karta",
            "kašel", "kaše", "káva", "kazeta", "kbelík",
            "kečup", "keř", "kino", "kladiv", "klec",
            "klenot", "kletba", "klíč", "klid", "klima",
            "kluk", "kmen", "kniha", "knoflík", "kočár",
            "kočka", "kohout", "koláč", "kolej", "kolo",
            "komín", "konec", "koník", "kopeč", "kopyt",
            "korek", "koření", "kořen", "košík", "kosti",
            "košile", "koza", "král", "kráva", "krev",
            "krize", "krok", "kruž", "křídlo", "kříž",
            "kuře", "kůra", "kůže", "kužel", "květy",
            "kytka", "labuť", "lampa", "láska", "látka",
            "lavic", "led", "léčba", "lehce", "lehký",
            "lékař", "lepek", "lesk", "letáč", "letad",
            "lidé", "liják", "lípa", "lísek", "list",
            "litr", "loď", "logik", "loket", "losos",
            "louka", "lůžko", "lyže", "malba", "malíř",
            "málo", "malý", "máma", "mapa", "maso",
            "máslo", "matka", "med", "medvě", "měsíc",
            "město", "metan", "metro", "meč", "midl",
            "milý", "mince", "míra", "mísa", "místo",
            "mléko", "mlha", "mluvit", "mladý", "mnoho",
            "mobil", "mocný", "model", "modrý", "mokrý",
            "motor", "mouka", "mozek", "mráz", "mrak",
            "mramor", "mrkat", "mrkev", "mrtvý", "mříž",
            "muška", "musli", "myš", "mýdlo", "mýtus",
            "nábytek", "náčrt", "nádob", "nádrž", "náhod",
            "nájem", "nákup", "náměs", "nápad", "nápoj",
            "národ", "nástroj", "nátek", "návod", "návyk",
            "nebez", "neděl", "nehod", "němec", "nést",
            "netop", "nevěs", "nikdo", "nikol", "nitro",
            "Nobel", "nohav", "norek", "normál", "nosič",
            "notář", "novín", "nový", "nůž", "nudle",
            "nula", "obálk", "oběd", "obchod", "obec",
            "oběť", "objev", "oblak", "oblek", "oblast",
            "obleč", "obraz", "obsah", "obsaz", "obtíž",
            "obyčej", "oceán", "ocet", "ocel", "ochran",
            "odběr", "odchod", "oddíl", "oděv", "odkaz",
            "odkud", "odlet", "odměn", "odmít", "odpad",
            "odpis", "odpor", "odpus", "odsud", "odvah",
            "odvět", "odvod", "odvoz", "ohař", "ohlas",
            "ohled", "ohnisk", "ohrad", "ohroč", "ohyb",
            "okamž", "okno", "okol", "okoun", "okres",
            "okruh", "okřík", "olej", "oliva", "olovo",
            "omáčk", "omezit", "omluv", "omyl", "opačn",
            "opak", "opasek", "opera", "opěrk", "opice",
            "oprav", "optik", "oráč", "oranž", "orati",
            "orel", "orgán", "orloj", "orlík", "ortel",
            "oslavit", "osoba", "osten", "ostře", "ostro",
            "ostud", "oštěp", "otáze", "otázk", "otec",
            "otisk", "otoč", "otrok", "otvír", "ovčín",
            "ovečk", "oves", "ovin", "ovlád", "ovoce",
            "ozdob", "pablo", "pacht", "padák", "padat",
            "padel", "pahýl", "pajda", "pakt", "palác",
            "palba", "palec", "paliv", "palma", "pálit",
            "památ", "pamět", "panel", "panen", "paník",
            "panna", "pánsk", "papež", "papír", "papri",
            "pára", "paráda", "pardál", "parfé", "parkán",
            "parní", "paroh", "parta", "partie", "paruk",
            "pařát", "páře", "pásek", "pásmo", "pasta",
            "patce", "paten", "páter", "pátky", "patro",
            "patron", "pažit", "pařát", "pecen", "pečet",
            "pečiv", "péče", "pedagog", "pedál", "pekař",
            "pékat", "peklo", "peníz", "penze", "pepře",
            "pera", "perla", "person", "peruť", "pěna",
            "pění", "pěst", "pěšák", "pětka", "pěvec",
            "piano", "pídit", "piják", "pikle", "pilot",
            "píle", "pílit", "písař", "písek", "píseň",
            "pískov", "pitka", "pitvá", "pivář", "pivot",
            "pivko", "plaid", "pláň", "plán", "planý",
            "plast", "plato", "plátě", "platí", "plató",
            "plávat", "plazy", "pláč", "pláče", "plec",
            "plemé", "ples", "plete", "pletý", "pleť",
            "pleva", "plich", "plíce", "plíše", "plnen",
            "plný", "ploce", "ploch", "plodek", "plodí",
            "plošn", "plout", "plová", "plovák", "plst",
            "pluh", "pluk", "plůdek", "plutov", "plvat",
            "plyn", "plyně", "počal", "počás", "počet",
            "počít", "počme", "počně", "počta", "podél",
            "podát", "poděl", "podíl", "podklad", "podle",
            "podne", "podob", "podol", "podpor", "podraz",
            "podří", "podstat", "podvad", "podvoz", "podzim",
            "pogro", "pohád", "pohan", "pohár", "pohled",
            "pohov", "pohro", "pohřb", "pohyb", "pochod",
            "pojat", "pojem", "pojis", "pojit", "pokaz",
            "poklád", "pokle", "pokol", "pokoř", "pokos",
            "pokož", "pokrč", "pokro", "pokrm", "pokut",
            "polář", "pole", "polej", "polen", "poleč",
            "polév", "police", "polit", "polka", "polní",
            "polno", "polom", "polonit", "polož", "pomád",
            "pomal", "poměr", "pomez", "pomlu", "pomně",
            "pomni", "pomoc", "pomost", "pompa", "pomst",
            "ponač", "poněk", "ponor", "ponos", "ponož",
            "popas", "popát", "popel", "popis", "popič",
            "pople", "popli", "popud", "popře", "popři",
            "porad", "poraz", "porod", "poros", "poroč",
            "poruč", "poruk", "poruš", "pořád", "pořez",
            "pořid", "posel", "posen", "posez", "posíl",
            "posil", "poskl", "posko", "poskr", "posla",
            "posle", "posluž", "posmě", "posta", "posto",
            "posun", "pošep", "poškr", "pošle", "pošli",
            "poštík", "pošva", "potác", "potaz", "poteč",
            "potěš", "potíc", "potka", "potla", "potmě",
            "potok", "poton", "potop", "potor", "potře",
            "potuc", "potuž", "potýč", "potýk", "pouč",
            "pouch", "poudr", "pouhe", "pouka", "pouko",
            "pouse", "pousť", "pouta", "pouto", "pouza",
            "použít", "považ", "povaz", "pověd", "pověra",
            "pověs", "povět", "pověz", "povez", "povin",
            "povla", "povle", "povli", "povlo", "povod",
            "povol", "povor", "povos", "povoz", "povrs",
            "povst", "povre", "povro", "povýš", "povzb",
            "povze", "povzn", "pozad", "pozám", "pozdě",
            "pozem", "pozla", "pozli", "pozlo", "pozme",
            "pozná", "pozně", "pozni", "pozor", "pozos",
            "pozva", "pozve", "požád", "požár", "požár",
            "požic", "požít", "poživ", "praba", "prach",
            "práce", "prací", "prádl", "práh", "prají",
            "prásk", "prato", "prává", "pravd", "pravo",
            "práže", "praže", "praží", "pražk", "prcek",
            "prcha", "prcht", "prémi", "prima", "princ",
            "prior", "prkno", "proch", "profi", "prohl",
            "proje", "propa", "prorok", "prosa", "prose",
            "prosi", "prosím", "proso", "prost", "proše",
            "proti", "prout", "prova", "prove", "provl",
            "provo", "proza", "proze", "prožd", "prože",
            "proži", "prsal", "prsen", "prsta", "první",
            "prvok", "pryč", "prýsk", "prýšt", "psali",
            "psal", "psane", "psaní", "psané", "psaž",
            "psík", "psota", "psten", "pstruh", "psych",
            "ptač", "ptáče", "pták", "ptáko", "ptali",
            "ptáme", "ptát", "publi", "pubes", "pučák",
            "puden", "pudín", "pudit", "pudlo", "pudová",
            "púč", "pudel", "pukač", "pukal", "pukan",
            "pukat", "pukl", "pukla", "pukle", "pukli",
            "pulce", "pulec", "pulír", "pulos", "půlka",
            "pulse", "půlí", "puman", "pumič", "pumpa",
            "pumpo", "punč", "punče", "punek", "puník",
            "purél", "purkr", "púsa", "půsat", "pusík",
            "pusit", "pusta", "puste", "pusti", "pusto",
            "pustý", "pustn", "pušče", "pušit", "pušká",
            "puška", "putát", "putik", "putna", "putni",
            "putov", "putře", "putří", "putýk", "putza",
            "pýcha", "pýří", "pýtal", "pytče", "pythá",
            "pytlá", "pytlé", "pýval", "pžená"
        ));
        
        for (String word : words) {
            wordSet.add(word.toLowerCase());
        }
    }
    
    private void categorizeWords() {
        for (String word : words) {
            char firstLetter = word.charAt(0);
            wordsByFirstLetter.computeIfAbsent(firstLetter, k -> new ArrayList<>()).add(word);
        }
    }
    
    private void calculateDifficulty() {
        Set<Character> commonLetters = new HashSet<>(Arrays.asList(
            'a', 'e', 'i', 'o', 'u', 'n', 's', 't', 'r', 'l'
        ));
        
        for (String word : words) {
            int difficulty = 5;
            Set<Character> uniqueLetters = new HashSet<>();
            
            for (char c : word.toCharArray()) {
                uniqueLetters.add(c);
                if (!commonLetters.contains(c)) {
                    difficulty += 1;
                }
            }
            
            if (uniqueLetters.size() == 5) {
                difficulty += 2;
            }
            
            wordDifficulty.put(word, difficulty);
        }
    }
    
    public String getRandomWord() {
        totalWordsUsed++;
        return words.get(random.nextInt(words.size()));
    }
    
    public String getRandomWordByDifficulty(int minDifficulty, int maxDifficulty) {
        java.util.List<String> filtered = words.stream()
            .filter(w -> {
                int diff = wordDifficulty.getOrDefault(w, 5);
                return diff >= minDifficulty && diff <= maxDifficulty;
            })
            .collect(Collectors.toList());
        
        if (filtered.isEmpty()) {
            return getRandomWord();
        }
        
        return filtered.get(random.nextInt(filtered.size()));
    }
    
    public String getRandomWordByFirstLetter(char letter) {
        java.util.List<String> filtered = wordsByFirstLetter.get(Character.toLowerCase(letter));
        if (filtered == null || filtered.isEmpty()) {
            return getRandomWord();
        }
        return filtered.get(random.nextInt(filtered.size()));
    }
    
    public boolean isValidWord(String word) {
        return wordSet.contains(word.toLowerCase());
    }
    
    public java.util.List<String> getAllWords() {
        return new ArrayList<>(words);
    }
    
    public int getWordCount() {
        return words.size();
    }
    
    public int getDifficulty(String word) {
        return wordDifficulty.getOrDefault(word.toLowerCase(), 5);
    }
    
    public int getTotalWordsUsed() {
        return totalWordsUsed;
    }
    
    public java.util.List<String> getWordsByPattern(String pattern) {
        java.util.List<String> matching = new ArrayList<>();
        
        for (String word : words) {
            if (matchesPattern(word, pattern)) {
                matching.add(word);
            }
        }
        
        return matching;
    }
    
    private boolean matchesPattern(String word, String pattern) {
        if (word.length() != pattern.length()) {
            return false;
        }
        
        for (int i = 0; i < word.length(); i++) {
            if (pattern.charAt(i) != '?' && pattern.charAt(i) != word.charAt(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    public java.util.List<String> getWordsContaining(char letter) {
        return words.stream()
            .filter(w -> w.indexOf(letter) >= 0)
            .collect(Collectors.toList());
    }
    
    public java.util.List<String> getWordsNotContaining(char letter) {
        return words.stream()
            .filter(w -> w.indexOf(letter) < 0)
            .collect(Collectors.toList());
    }
    
    public Map<Character, Integer> getLetterFrequency() {
        Map<Character, Integer> frequency = new HashMap<>();
        
        for (String word : words) {
            for (char c : word.toCharArray()) {
                frequency.put(c, frequency.getOrDefault(c, 0) + 1);
            }
        }
        
        return frequency;
    }
    
    public java.util.List<String> getEasyWords() {
        return getRandomWordsByDifficultyRange(1, 6, 50);
    }
    
    public java.util.List<String> getMediumWords() {
        return getRandomWordsByDifficultyRange(7, 9, 50);
    }
    
    public java.util.List<String> getHardWords() {
        return getRandomWordsByDifficultyRange(10, 15, 50);
    }
    
    private java.util.List<String> getRandomWordsByDifficultyRange(int min, int max, int count) {
        java.util.List<String> filtered = words.stream()
            .filter(w -> {
                int diff = wordDifficulty.getOrDefault(w, 5);
                return diff >= min && diff <= max;
            })
            .limit(count)
            .collect(Collectors.toList());
        
        Collections.shuffle(filtered, random);
        return filtered;
    }
    
    public String getWordInfo(String word) {
        word = word.toLowerCase();
        if (!isValidWord(word)) {
            return "Slovo není v seznamu";
        }
        
        int difficulty = getDifficulty(word);
        Set<Character> uniqueLetters = new HashSet<>();
        for (char c : word.toCharArray()) {
            uniqueLetters.add(c);
        }
        
        return String.format("Slovo: %s | Obtížnost: %d/15 | Unikátní písmena: %d/5",
            word.toUpperCase(), difficulty, uniqueLetters.size());
    }
    
    public void resetUsageCount() {
        totalWordsUsed = 0;
    }
}
