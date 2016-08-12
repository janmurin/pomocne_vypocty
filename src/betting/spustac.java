package betting;

import betting.entities.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jan.murin on 10-Aug-16.
 */
public class spustac {


    private List<Country> countries;
    private List<Sport> sports;
    private List<League> leagues;
    private List<CleanBetEvent> eventyMesiaca;
    private List<EventType> eventTypes;

    public static void main(String[] args) {
        spustac spustac = new spustac();
        spustac.createMockupDB2();
    }

    private void loadEntities() {
        ObjectMapper mapper = new ObjectMapper();

        eventyMesiaca = new ArrayList<>();
        sports = new ArrayList<>();
        leagues = new ArrayList<>();
        countries = new ArrayList<>();

        try {
            countries = mapper.readValue(new File("src\\betting\\data\\countries.json"), new TypeReference<List<Country>>() {
            });
            sports = mapper.readValue(new File("src\\betting\\data\\sports.json"), new TypeReference<List<Sport>>() {
            });
            leagues = mapper.readValue(new File("src\\betting\\data\\leagues.json"), new TypeReference<List<League>>() {
            });
            eventTypes = mapper.readValue(new File("src\\betting\\data\\eventTypes.json"), new TypeReference<List<EventType>>() {
            });
            eventyMesiaca = mapper.readValue(new File("C:\\Users\\jan.murin\\Google Drive\\mockupDB.json"), new TypeReference<List<CleanBetEvent>>() {
            });
        } catch (IOException ex) {
            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("eventy size: " + eventyMesiaca.size());
        System.out.println("sports size: " + sports.size() + " nacitane sports: " + sports);
        System.out.println("leagues size: " + leagues.size() + " nacitane leagues: " + leagues);
        System.out.println("countries size: " + countries.size() + " countries: " + countries);
        System.out.println("eventTypes size: " + eventTypes.size() + " eventTypes: " + eventTypes);
    }

    public void createMockupDB2() {

        loadEntities();
        // mame nacitane podporovane sports, ligy, eventy a ideme vybrat tie udalosti z databazy,
        // ktore obsahuju vsetky nase podporovane polozky
        List<UnsupportedBetEvent> unsupportedEvents = new ArrayList<>();
        Set<String> unsupportedSports = new HashSet<>();
        Set<String> unsupportedLeagues = new HashSet<>();
        Set<String> unsupportedEventTypes = new HashSet<>();
        // Set<String> unsupportedCompetitors = new HashSet<>();
        List<CleanBetEvent> supportedEvents = new ArrayList<>();
        List<Competitor> competitors = new ArrayList<>();
        Map<String, Map<String, Set<String>>> competitorsMap = new HashMap<>();
        for (Sport s : sports) {
            competitorsMap.put(s.name_SK, new HashMap<String, Set<String>>());
        }
        for (League league : leagues) {
            Sport s = getSportFromID(league);
            competitorsMap.get(s.name_SK).put(league.name_SK, new HashSet<String>());
        }
        competitorsMap.get("Tenis").put("Tenis", new HashSet<String>());
        System.out.println(competitorsMap);

        for (CleanBetEvent cbe : eventyMesiaca) {
            int competitorID = 0;
            Sport sport = getSportForBetEvent(cbe);
            League league = getLeagueForBetEvent(cbe);
            EventType eventType = getEventTypeForBetEvent(cbe);
            // competitorID = getCompetitorID(cbe); COMPETITOR JE DEFAULTNE PODPOROVANY, JEDINE ZE JE DUPLICITA
            boolean supported = true;
            // ak nemame podporovany sport, typ udalosti alebo competitora tak je cely event nepodporovany
            // ak si nejaky sport vyzaduje aj podporu pre ligu
            if (sport == null || eventType == null || competitorID == -1) {
                supported = false;
            }
            if (sport.leagueRequired && league == null) {
                supported = false;
                unsupportedLeagues.add(sport.name_SK + " " + cbe.getLiga());
            }
            if (sport == null) {
                unsupportedSports.add(cbe.getSport());
            }
            if (eventType == null) {
                unsupportedEventTypes.add(cbe.getTypEventu());
            }
            if (!supported) {
                UnsupportedBetEvent ube = new UnsupportedBetEvent();
                ube.betEvent = cbe;
                ube.isCompetitorSupported = (competitorID != -1);
                ube.isEventTypeSupported = (eventType != null);
                ube.isLeagueSupported = (league != null);
                ube.isSportSupported = (sport != null);
                unsupportedEvents.add(ube);
            } else {
                supportedEvents.add(cbe);
            }
            // ked je sport aj liga supportovana, tak automaticky pridame competitora
            if (sport != null) {
                if (league != null) {
                    // pridame competitora ku sportu aj lige
                    String[] competitori = parseCompetitors(cbe.getCompetitors());
                    for (int i = 0; i < competitori.length; i++) {
                        if (!competitorsMap.get(sport.name_SK).get(league.name_SK).contains(competitori[i])) {
                            competitorsMap.get(sport.name_SK).get(league.name_SK).add(competitori[i]);
                            Competitor novy = new Competitor();
                            novy.league_ID = league.id;
                            novy.sport_ID = sport.id;
                            novy.name = competitori[i];
                            competitors.add(novy);
                        }
                    }
                } else if (!sport.leagueRequired) {
                    // ak podporujeme sport ale nepodporujeme ligu, tak ak ligu nemusime podporovat (napriklad Tenis),
                    // tak este mozeme tiez pridat competitora, ale u competitora uz je liga irelevantna
                    String[] competitori = parseCompetitors(cbe.getCompetitors());
                    for (int i = 0; i < competitori.length; i++) {
                        try {
                            if (!competitorsMap.get(sport.name_SK)
                                    .get(sport.name_SK)
                                    .contains(competitori[i])) {
                                competitorsMap.get(sport.name_SK).get(sport.name_SK).add(competitori[i]);
                                Competitor novy = new Competitor();
                                //novy.league_ID = league.id; //ligu nam netreba lebo je to tenis napriklad a tma je liga nepodstatna
                                novy.sport_ID = sport.id;
                                novy.name = competitori[i];
                                competitors.add(novy);
                            }
                        } catch (Exception e) {
                            System.out.println("sportname: " + sport.name_SK);
                            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, e);
                            throw e;
                        }
                    }
                }
            }
        }

        System.out.println("UNSUPPORTED");
        System.out.println("SPORTS " + unsupportedSports.size());
        for (String s : unsupportedSports) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("LEAGUES " + unsupportedLeagues.size());
        for (String s : unsupportedLeagues) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("EVENT TYPES " + unsupportedEventTypes.size());
        for (String s : unsupportedEventTypes) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("COMPETITORS " + competitors.size());
        int val = 1;
        for (Competitor c : competitors) {
            c.id = val;
            System.out.println(c);
            val++;
        }

    }

    private Sport getSportFromID(League league) {
        for (Sport s : sports) {
            if (s.id == league.sport_ID) {
                return s;
            }
        }
        return null;
    }

    private String[] parseCompetitors(String competitors) {
        String s = competitors.split("    ")[0];
        int coutn = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '-') {
                coutn++;
            }
        }
        if (coutn != 1) {
            System.out.println("ZLY POCET POMLCIEK: [" + s + "] pocet: " + coutn);
        }
        String[] comp = new String[2];
        s = s.substring(s.indexOf(" ")).trim();
        comp[0] = s.substring(0, s.indexOf("-")).trim();
        comp[1] = s.substring(s.indexOf("-") + 1).trim();
        return comp;
    }

    private int getCompetitorID(CleanBetEvent cbe) {
//        for (EventType et : eventTypes) {
//            if (et.name_SK.equals(cbe.getTypEventu())) {
//                return et.id;
//            }
//        }
        return -1;
    }

    private EventType getEventTypeForBetEvent(CleanBetEvent cbe) {
        for (EventType et : eventTypes) {
            if (et.name_SK.equals(cbe.getTypEventu())) {
                return et;
            }
        }
        return null;
    }

    private League getLeagueForBetEvent(CleanBetEvent cbe) {
        for (League lg : leagues) {
            if (lg.name_SK.equals(cbe.getLiga())) {
                return lg;
            }
        }
        return null;
    }

    private Sport getSportForBetEvent(CleanBetEvent sport) {
        for (Sport sp : sports) {
            if (sp.name_SK.equals(sport.getSport())) {
                return sp;
            }
        }
        return null;
    }
}
    /*
    private Sport getSport(String sport, ArrayList<Sport> sporty) {
        for (Sport sp : sporty) {
            if (sp.name_SK.equals(sport)) {
                return sp;
            }
        }
        return null;
    }

    private Country getCountry(String s, ArrayList<Country> countries) {
        Country maxCountry = null;
        int max = 0;
        for (Country c : countries) {
            int lcs = lcs(c.name_SK, s).length();
            if (lcs > max) {
                max = lcs;
                maxCountry = c;
            }
        }

        return maxCountry;
    }

    public static String lcs(String a, String b) {
        int[][] lengths = new int[a.length() + 1][b.length() + 1];

        // row 0 and column 0 are initialized to 0 already

        for (int i = 0; i < a.length(); i++)
            for (int j = 0; j < b.length(); j++)
                if (a.charAt(i) == b.charAt(j))
                    lengths[i + 1][j + 1] = lengths[i][j] + 1;
                else
                    lengths[i + 1][j + 1] =
                            Math.max(lengths[i + 1][j], lengths[i][j + 1]);

        // read the substring out from the matrix
        StringBuffer sb = new StringBuffer();
        for (int x = a.length(), y = b.length();
             x != 0 && y != 0; ) {
            if (lengths[x][y] == lengths[x - 1][y])
                x--;
            else if (lengths[x][y] == lengths[x][y - 1])
                y--;
            else {
                assert a.charAt(x - 1) == b.charAt(y - 1);
                sb.append(a.charAt(x - 1));
                x--;
                y--;
            }
        }

        return sb.reverse().toString();
    }

    private static boolean isValidSport(String sport, ArrayList<Sport> sporty) {
        for (Sport s : sporty) {
            if (s.belongs(sport)) {
                //System.out.println("zhoda: ["+s.name_SK+"] z ["+sport+"]");
                return true;
            }
        }
        return false;
    }

    private static boolean isOKSport(String sport) {
        if (sport.equalsIgnoreCase("EXPERT") ||
                sport.equalsIgnoreCase("Futbal-ME 2016") ||
                sport.equalsIgnoreCase("Favorit dňa") ||
                sport.equalsIgnoreCase("Duel")) {
            return false;
        }
        return true;
    }

    private static boolean isOK(String poznamka) {
        if (poznamka.equalsIgnoreCase("1") ||
                poznamka.equalsIgnoreCase("2") ||
                poznamka.equalsIgnoreCase("0") ||
                poznamka.equalsIgnoreCase("10") ||
                poznamka.equalsIgnoreCase("12") ||
                poznamka.equalsIgnoreCase("02")) {
            return true;
        }
        return false;
    }
//
//
//    public void createMockupDB(){
//        ObjectMapper mapper = new ObjectMapper();
//        ArrayList<Sport> sports = new ArrayList<>();
//        try {
//            sports = mapper.readValue(new File("src\\betting\\data\\sports.json"), new TypeReference<List<Sport>>() {
//            });
//        } catch (IOException ex) {
//            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.println("nacitane sports: " + sports);
//
//
//        ArrayList<CleanBetEvent> eventyMesiaca = new ArrayList<>();
//        try {
//            eventyMesiaca = mapper.readValue(new File("C:\\Users\\jan.murin\\Google Drive\\vybrane.json"), new TypeReference<List<CleanBetEvent>>() {
//            });
//        } catch (IOException ex) {
//            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        System.out.println("nacitanych eventov: " + eventyMesiaca.size());
//        List<CleanBetEvent> vybrane = new ArrayList<>();
////        Set<String> sports = new HashSet<>();
////        Set<String> ligy = new HashSet<>();
//        Map<String, Set<String>> mapa = new HashMap<>();
//        Set<String> poznamky = new HashSet<>();
//        Set<String> neidentifikovaneSporty = new HashSet<>();
//        Set<String> validneSporty = new HashSet<>();
//
//        for (CleanBetEvent cbe : eventyMesiaca) {
//            if (cbe.getTypEventu().equalsIgnoreCase("Zápas") && isOK(cbe.getPoznamka()) && isOKSport(cbe.getSport())) {
//                poznamky.add(cbe.getPoznamka());
//                if (!isValidSport(cbe.getSport().trim(), sports)) {
//                    neidentifikovaneSporty.add(cbe.getSport());
//                } else {
//                    validneSporty.add(cbe.getSport());
//                    vybrane.add(cbe);
//                    if (mapa.containsKey(cbe.getSport())) {
//                        mapa.get(cbe.getSport()).add(cbe.getLiga());
//                    } else {
//                        Set<String> setik = new HashSet<>();
//                        setik.add(cbe.getLiga());
//                        mapa.put(cbe.getSport(), setik);
//                    }
//                }
//            }
//
//        }
//        System.out.println("velkost vybranych: " + vybrane.size());
//        System.out.println("sports size: " + mapa.keySet().size() + " ");
//        for (String sport : mapa.keySet()) {
//            System.out.println("SPORT: " + sport + "    pocet lig: " + mapa.get(sport).size());
//            for (String s : mapa.get(sport)) {
//                //System.out.println("    " + s);
//            }
//        }
////
//        for(CleanBetEvent cbe:vybrane){
//            System.out.println(cbe);
//        }
//        System.out.println("poznamky:\n" + poznamky);
//        System.out.println("neidentifikovane sports: " + neidentifikovaneSporty);
//        System.out.println("validne sports: " + validneSporty);
//    }
}

/*
        // zistime ake mame ligy
        //Set<String> ligy=new HashSet<>();
        Map<String, Set<String>> mapa = new HashMap<>();
        for (CleanBetEvent cbe : eventyMesiaca) {
            //ligy.add(cbe.getLiga());
            if (mapa.containsKey(cbe.getSport())) {
                mapa.get(cbe.getSport()).add(cbe.getLiga());
            } else {
                Set<String> setik = new HashSet<>();
                setik.add(cbe.getLiga());
                mapa.put(cbe.getSport(), setik);
            }
        }


        System.out.println("sports size: " + mapa.keySet().size() + " ");
        int id = 1;
        for (String sport : mapa.keySet()) {
            System.out.println("SPORT: [" + sport + "]    pocet lig: " + mapa.get(sport).size());
            for (String s : mapa.get(sport)) {
                if (sport.equals("Futbal") || sport.equals("Hokej")) {
                    System.out.println("{\"id\":\"" + id + "\",\"name_SK\":\"" + s + "\",\"country_ID\":\"" + getCountry(s, countries).id + "\",\"country_name\":\"" + getCountry(s, countries).name_SK + "\",\"sport_ID\":\"" + getSport(sport, sports).id + "\"},");
                    id++;
                } else {
                    System.out.println("    " + s + " => ");
                }
            }
        }

        for (int i = 1; i < leagues.size(); i++) {
            leagues.get(i - 1).id = i;
            System.out.println(leagues.get(i - 1) + ",");
        }

//        for(String s:ligy){
//            System.out.println(s);
//        }


*/
