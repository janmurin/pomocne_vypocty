package betting.entities;

/**
 * Created by jan.murin on 12-Aug-16.
 */
public class Competitor {
    public int id;
    public String name;
    public int sport_ID;
    public int league_ID;
    public Sport sport;
    public League league;

    @Override
    public String toString() {
        return "{\"" +
                "\"id\"=\"" + id +
                "\", \"name\"=\"" + name +
                "\", \"sport_ID\"=\"" + sport_ID +
                "\", \"league_ID\"=\"" + league_ID +"\"}";
    }
}
