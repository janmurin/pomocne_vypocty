package betting.entities;

/**
 * Created by jan.murin on 12-Aug-16.
 */
public class EventType {

    public int id;
    public String name;
    public String name_SK;

    @Override
    public String toString() {
        return "EventType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", name_SK='" + name_SK + '\'' +
                '}';
    }
}
