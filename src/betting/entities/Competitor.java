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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Competitor that = (Competitor) o;

        if (sport_ID != that.sport_ID) return false;
        if (league_ID != that.league_ID) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + sport_ID;
        result = 31 * result + league_ID;
        return result;
    }

    @Override
    public String toString() {
        return "{\"id\"=\"" + id +
                "\", \"name\"=\"" + name +
                "\", \"sport_ID\"=\"" + sport_ID +
                "\", \"league_ID\"=\"" + league_ID +"\"}";
    }
}
