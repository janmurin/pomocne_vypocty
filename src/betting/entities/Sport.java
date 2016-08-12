package betting.entities;

/**
 * Created by jan.murin on 11-Aug-16.
 */
public class Sport {

    public int id;
    public String name;
    public String name_SK;
    public boolean leagueRequired;

    public boolean belongs(String sport) {
        if (sport.equalsIgnoreCase(name)
                || sport.equalsIgnoreCase(name_SK)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", name_SK='" + name_SK + '\'' +
                '}';
    }
}
