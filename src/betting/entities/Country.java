package betting.entities;

/**
 * Created by jan.murin on 12-Aug-16.
 */
public class Country {

    public int id;
    public String name;
    public String name_SK;

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", name_SK='" + name_SK + '\'' +
                '}';
    }
}
