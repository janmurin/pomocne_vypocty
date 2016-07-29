package modlitby;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jan.murin on 25-Jul-16.
 */
public class Modlitba {

    @JsonProperty("class")
    String trieda;
    String title;
    String content;

    public String getTrieda() {
        return trieda;
    }

    public void setTrieda(String trieda) {
        this.trieda = trieda;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
