package hrad;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by jan.murin on 25-Jul-16.
 */
public class Spustac {


    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File("src/hrad/topics.json");
        String dummyContent = "dummy.html";

        try {
            List<Topic> topics = objectMapper.readValue(file, new TypeReference<List<Topic>>() {
            });
            System.out.println("topics length: " + topics.size());

            for (int i = 0; i < topics.size(); i++) {
                Topic m = topics.get(i);
                System.out.println("Topic m" + i + " = new Topic();");
                System.out.println("m" + i + ".name=\"" + m.name + "\";");
                for (int j = 0; j < m.articles.size(); j++) {
                    Article a = m.articles.get(j);
                    System.out.println("Article a" + i + "" + j + " = new Article();");
                    System.out.println("a" + i + "" + j + ".title=\"" + a.title + "\";");
                    System.out.println("a" + i + "" + j + ".tabName=\"" + a.tabName + "\";");
                    if (a.assetUrl.length() < 1) {
                        System.out.println("a" + i + "" + j + ".assetUrl=\"" + dummyContent.replaceAll("\"", "\\\\\"") + "\";");
                    } else {
                        System.out.println("a" + i + "" + j + ".assetUrl=\"" + a.assetUrl.replaceAll("\"", "\\\\\"") + "\";");
                    }
                    for(int k=0; k<a.images.size(); k++){
                        System.out.println("a" + i + "" + j + ".images.add(new Image(\""+a.images.get(k).name+"\",\"" + a.images.get(k).title + "\") );");
                    }
                    System.out.println("m" + i + ".articles.add(a" + i + "" + j + ");");
                }
                System.out.println("topics.add(m" + i + ");");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
