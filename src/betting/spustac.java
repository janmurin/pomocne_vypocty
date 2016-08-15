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
    private List<Competitor> competitors;

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
        competitors = new ArrayList<>();

        try {
            countries = mapper.readValue(new File("src\\betting\\data\\countries.json"), new TypeReference<List<Country>>() {
            });
            sports = mapper.readValue(new File("src\\betting\\data\\sports.json"), new TypeReference<List<Sport>>() {
            });
            leagues = mapper.readValue(new File("src\\betting\\data\\leagues.json"), new TypeReference<List<League>>() {
            });
            eventTypes = mapper.readValue(new File("src\\betting\\data\\eventTypes.json"), new TypeReference<List<EventType>>() {
            });
            competitors = mapper.readValue(new File("src\\betting\\data\\competitors.json"), new TypeReference<List<Competitor>>() {
            });
            //eventyMesiaca = mapper.readValue(new File("C:\\Users\\jan.murin\\Google Drive\\mockupDB.json"), new TypeReference<List<CleanBetEvent>>() {
            eventyMesiaca = mapper.readValue(new File("C:\\Users\\janmu\\Google Drive\\mockupDB.json"), new TypeReference<List<CleanBetEvent>>() {
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
        Set<String> unsupportedSports = new HashSet<>();
        Set<String> unsupportedLeagues = new HashSet<>();
        Set<String> unsupportedEventTypes = new HashSet<>();
        Set<String> unsupportedCompetitors = new HashSet<>();
        List<SupportedBetEvent> supportedEvents = new ArrayList<>();


        for (CleanBetEvent cbe : eventyMesiaca) {
            // hlavnym cielom je zistit ci tento event je podporovany a ak ano tak ho pridat do zoznamu eventov
            boolean supported = true;
            Sport sport = getSportForBetEvent(cbe);
            League league = getLeagueForBetEvent(cbe);
            EventType eventType = getEventTypeForBetEvent(cbe);

            if (sport == null) {
                // ak sa nepodporuje dany sport tak ostatne nema zmysel riesit
                supported = false;
                unsupportedSports.add(cbe.getSport());
            } else {
                // mame sport, ak sa vyzaduje liga tak musime mat ligu, ak nie tak ligu neriesime
                if (league == null) {
                    // nemame ligu, bud taku ligu nemame v databaze alebo pre nas nie je relevantna napriklad pri tenise
                    if (sport.leagueRequired) {
                        supported = false;
                        unsupportedLeagues.add(sport.name_SK + " " + cbe.getLiga());
                    } else {
                        // ligu neriesime, spravime si defaultnu ligu
                        league = new League();
                        league.id = 0;
                    }
                } else {
                    // sport aj liga eventu su sparovane so sportom aj ligou v databaze
                }
            }
            if (supported) {
                // mame sport aj ligu podporovanu, zistime ci je aj eventtype podporovany
                if (eventType == null) {
                    supported = false;
                    unsupportedEventTypes.add(cbe.getTypEventu());
                } else {
                    // mame podporovany sport, ligu aj eventtype, uz len aby boli podporovani competitori a mozeme pridat do zoznamu event
                    Competitor[] competitors = getSupportedCompetitors(cbe, league, sport);
                    if (competitors[0].id == 0 || competitors[1].id == 0) { // musia mat id>0 inac su to defaultni competitori a nie ti z databazy
                        //supported=false; uz nam netreba pridavat lebo s tym viacej nepracujeme
                        if (competitors[0].id == 0) {
                            unsupportedCompetitors.add("sport=" + sport.name_SK + ", league=" + league.name_SK + ", competitor=" + competitors[0].name + "<-");
                        }
                        if (competitors[1].id == 0) {
                            unsupportedCompetitors.add("sport=" + sport.name_SK + ", league=" + league.name_SK + ", competitor=" + competitors[1].name + "<-");
                        }
                    } else {
                        supportedEvents.add(new SupportedBetEvent(sport, league, eventType, competitors[0], competitors[1], cbe));
                    }
                }
            }
        }

        // povypisujeme co vsetko nemame podporovane
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
        System.out.println("COMPETITORS " + unsupportedCompetitors.size());
        for (String c : unsupportedCompetitors) {
            System.out.println(c);
        }

        // mame sparsovane eventu z databazy, v zozname mame iba podporovane entity, teraz z nich vytvorit databazu pre REST server
        System.out.println();
        System.out.println("nacitanych podporovanych CleanBetEventov: " + supportedEvents.size());


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
            throw new RuntimeException();
        }
        String[] comp = new String[2];
        s = s.substring(s.indexOf(" ")).trim();
        comp[0] = s.substring(0, s.indexOf("-")).trim();
        comp[1] = s.substring(s.indexOf("-") + 1).trim();
        return comp;
    }

    private Competitor[] getSupportedCompetitors(CleanBetEvent cbe, League league, Sport sport) {
        if (league == null || sport == null) {
            throw new NullPointerException("league alebo sport je NULL");
        }
        // vyparsujeme competitorov a zistime ci existuju nejaki neplatni
        String[] strings = parseCompetitors(cbe.getCompetitors());
        Competitor[] supported = new Competitor[2];
        // competitori sa porovnavaju iba podla name, id sportu a id ligy
        Competitor c1 = new Competitor();
        c1.name = strings[0];
        c1.league_ID = league.id;
        c1.sport_ID = sport.id;
        Competitor c2 = new Competitor();
        c2.name = strings[1];
        c2.league_ID = league.id;
        c2.sport_ID = sport.id;
        // nastavime hladaneho competitora aby ked sa nenajde sme vedeli jeho meno si zaznamenat aspon
        supported[0] = c1;
        supported[1] = c2;
        for (Competitor c : competitors) {
            if (c1.equals(c)) {
                supported[0] = c;
            }
            if (c2.equals(c)) {
                supported[1] = c;
            }
        }
        return supported;
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
       // init competitors map pre potreby pridavania vsetkych competitorov, ked uz bude nejaka databaza competitorov tak sa competitori budu overovat ci nie su duplikaty existujucich len inaksie pomenovani
//        Map<String, Map<String, Set<String>>> competitorsMap = new HashMap<>();
//        for (Sport s : sports) {
//            competitorsMap.put(s.name_SK, new HashMap<String, Set<String>>());
//        }
//        for (League league : leagues) {
//            Sport s = getSportFromID(league);
//            competitorsMap.get(s.name_SK).put(league.name_SK, new HashSet<String>());
//        }
//        competitorsMap.get("Tenis").put("Tenis", new HashSet<String>());
//        System.out.println(competitorsMap);
                // PRIDAVANIE COMPETITOROV- len treba overit ci uz nepridavame tych istych
            // ked je sport aj liga supportovana, tak automaticky pridame competitora
//            if (sport != null) {
//                if (league != null) {
//                    // pridame competitora ku sportu aj lige
//                    String[] competitori = parseCompetitors(cbe.getUnsupportedCompetitors());
//                    for (int i = 0; i < competitori.length; i++) {
//                        if (!competitorsMap.get(sport.name_SK).get(league.name_SK).contains(competitori[i])) {
//                            competitorsMap.get(sport.name_SK).get(league.name_SK).add(competitori[i]);
//                            Competitor novy = new Competitor();
//                            novy.league_ID = league.id;
//                            novy.sport_ID = sport.id;
//                            novy.name = competitori[i];
//                            competitors.add(novy);
//                        }
//                    }
//                } else if (!sport.leagueRequired) {
//                    // ak podporujeme sport ale nepodporujeme ligu, tak ak ligu nemusime podporovat (napriklad Tenis),
//                    // tak este mozeme tiez pridat competitora, ale u competitora uz je liga irelevantna
//                    String[] competitori = parseCompetitors(cbe.getUnsupportedCompetitors());
//                    for (int i = 0; i < competitori.length; i++) {
//                        try {
//                            if (!competitorsMap.get(sport.name_SK)
//                                    .get(sport.name_SK)
//                                    .contains(competitori[i])) {
//                                competitorsMap.get(sport.name_SK).get(sport.name_SK).add(competitori[i]);
//                                Competitor novy = new Competitor();
//                                //novy.league_ID = league.id; //ligu nam netreba lebo je to tenis napriklad a tma je liga nepodstatna
//                                novy.sport_ID = sport.id;
//                                novy.name = competitori[i];
//                                competitors.add(novy);
//                            }
//                        } catch (Exception e) {
//                            System.out.println("sportname: " + sport.name_SK);
//                            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, e);
//                            throw e;
//                        }
//                    }
//                }
//            }






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
