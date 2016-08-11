package betting;

import betting.entities.CleanBetEvent;
import betting.entities.Sport;
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

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Sport> sporty = new ArrayList<>();
        try {
            sporty = mapper.readValue(new File("src\\betting\\data\\sports.json"), new TypeReference<List<Sport>>() {
            });
        } catch (IOException ex) {
            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("nacitane sporty: "+sporty);


        ArrayList<CleanBetEvent> eventyMesiaca = new ArrayList<>();
        try {
            eventyMesiaca = mapper.readValue(new File("C:\\Users\\jan.murin\\Google Drive\\2016-07.json"), new TypeReference<List<CleanBetEvent>>() {
            });
        } catch (IOException ex) {
            Logger.getLogger(spustac.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("nacitanych eventov: " + eventyMesiaca.size());
        List<CleanBetEvent> vybrane = new ArrayList<>();
//        Set<String> sporty = new HashSet<>();
//        Set<String> ligy = new HashSet<>();
        Map<String, Set<String>> mapa = new HashMap<>();
        Set<String> poznamky = new HashSet<>();
        Set<String> neidentifikovaneSporty = new HashSet<>();
        Set<String> validneSporty = new HashSet<>();

        for (CleanBetEvent cbe : eventyMesiaca) {
            if (cbe.getTypEventu().equalsIgnoreCase("Zápas") && isOK(cbe.getPoznamka()) && isOKSport(cbe.getSport())) {
                vybrane.add(cbe);
                if (mapa.containsKey(cbe.getSport())) {
                    mapa.get(cbe.getSport()).add(cbe.getLiga());
                } else {
                    Set<String> setik = new HashSet<>();
                    setik.add(cbe.getLiga());
                    mapa.put(cbe.getSport(), setik);
                }
                poznamky.add(cbe.getPoznamka());
                if (!isValidSport(cbe.getSport().trim(), sporty)) {
                    neidentifikovaneSporty.add(cbe.getSport());
                }else{
                    validneSporty.add(cbe.getSport());
                }
            }

        }
        System.out.println("velkost vybranych: " + vybrane.size());
        System.out.println("sporty size: " + mapa.keySet().size() + " ");
        for (String sport : mapa.keySet()) {
            System.out.println("SPORT: " + sport + "    pocet lig: " + mapa.get(sport).size());
            for (String s : mapa.get(sport)) {
                //System.out.println("    " + s);
            }
        }
//
//        for(CleanBetEvent cbe:vybrane){
//            System.out.println(cbe);
//        }
        System.out.println("poznamky:\n" + poznamky);
        System.out.println("neidentifikovane sporty: " + neidentifikovaneSporty);
        System.out.println("validne sporty: " + validneSporty);
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


}
