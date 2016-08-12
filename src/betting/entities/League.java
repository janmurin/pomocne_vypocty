package betting.entities;

/**
 * Created by jan.murin on 12-Aug-16.
 */
public class League {
    public int id;
    public String name_SK;
    public int country_ID;
    public String country_name;
    public int sport_ID;

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id +
                "\", \"name_SK\":\"" + name_SK  +
                "\", \"country_ID\":\"" + country_ID +
                "\", \"sport_ID\":\"" + sport_ID +
                "\"}";
    }
}
