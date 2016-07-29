package modlitby;

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

        File file = new File("src/modlitby.json");

        try {
            List<Modlitba> modlitby = objectMapper.readValue(file, new TypeReference<List<Modlitba>>() {
            });
            System.out.println("modlitby length: " + modlitby.size());

            for (int i = 0; i < modlitby.size(); i++) {
                Modlitba m = modlitby.get(i);
                System.out.println("Modlitba m" + i + " = new Modlitba();");
                System.out.println("m" + i + ".setTrieda(\"" + m.getTrieda() + "\");");
                System.out.println("m" + i + ".setTitle(\"" + m.getTitle() + "\");");
                System.out.print("m" + i + ".setContent(\"");
                String pom=m.getContent().replaceAll("\"","\\\\\"");
                System.out.print(pom);
                System.out.println("\");");
                System.out.println("modlitby.add(m" + i + ");");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
