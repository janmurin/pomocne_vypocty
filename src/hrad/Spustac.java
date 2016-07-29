package hrad;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import modlitby.Modlitba;

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
        String dummyContent = "<h2>DUMMY CONTENT</h2><p>&nbsp;</p><p>Prv&aacute; p&iacute;somn&aacute; zmienka o majetku Čičva poch&aacute;dza z roku 1270. Ide o donačn&uacute; listinu uhorsk&eacute;ho kr&aacute;ľa &Scaron;tefana V., ktorou &scaron;ľachticovi Reynoldovi, za vern&eacute; služby a z&aacute;sluhy preuk&aacute;zan&eacute; na v&yacute;prav&aacute;ch kr&aacute;ľovsk&eacute;ho vojska daroval viacer&eacute; majetky v Abovskej, Sabolčskej a Zempl&iacute;nskej župe, vr&aacute;tane majetku Čičva spolu s dedinami Dlh&eacute; Pole a Vi&scaron;ňov. Prv&aacute; p&iacute;somn&aacute; zmienka o hrade rovnak&eacute;ho mena poch&aacute;dza z roku 1316. Jedn&aacute; sa o listinu, ktorou magister Peter, pravdepodobne Reynoldov vnuk, odmenil Mikul&aacute;&scaron;a Peresa, kastel&aacute;na na hrade Čičva, majetkom Tuzs&eacute;r za &uacute;spe&scaron;n&uacute; obranu hradu. Podľa v&scaron;etk&eacute;ho i&scaron;lo o odrazenie &uacute;toku Petra, syna Petra Peteňa, zempl&iacute;nskeho &uacute;častn&iacute;ka povstania proti kr&aacute;ľovi, pri ktorom kastel&aacute;n Mikul&aacute;&scaron; stratil ľav&uacute; ruku a jeho brat &Scaron;tefan i traja ďal&scaron;&iacute; členovia hradnej pos&aacute;dky pri&scaron;li o život.</p><p>Dodnes nepozn&aacute;me presn&yacute; rok a okolnosti v&yacute;stavby hradu Čičva. Vďaka strategickej polohe kopca, na ktorom stoj&iacute; ho mohli postaviť z iniciat&iacute;vy uhorsk&yacute;ch kr&aacute;ľov už v 12. storoč&iacute;, ako s&uacute;časť pohraničn&eacute;ho pevnostn&eacute;ho syst&eacute;mu Uhorsk&eacute;ho kr&aacute;ľovstva proti možn&yacute;m vojensk&yacute;m vp&aacute;dom zo zahraničia. V 12. storoč&iacute; sa severn&eacute; hranice Uhorsk&eacute;ho kr&aacute;ľovstva tiahli od Vihorlatsk&yacute;ch vrchov cez Str&aacute;žske, Vranov, Hanu&scaron;ovce do Kapu&scaron;ian pri Pre&scaron;ove a ďalej na z&aacute;pad. Na tejto l&iacute;nii zotrvali pravdepodobne až do konca 13. storočia.</p><p>Kr&aacute;ľ Žigmund v listin&aacute;ch z rokov 1410 a 1414, ktor&yacute;mi potvrdil Rozgonyiovcom vlastn&iacute;ctvo majetkov panstva Čičva a donačn&uacute; listinu z roku 1270, uv&aacute;dza, že s&uacute;časťou panstva je hrad Čičva, ktor&yacute; dali postaviť ich predkovia. T&aacute;to inform&aacute;cia n&aacute;s vedie ku kon&scaron;tatovaniu, že hrad Čičva dal postaviť Reynold alebo jeho potomkovia, možno syn Ladislav, či vnuk Peter. Pravdepodobnej&scaron;ie je, že hrad na tak strategickom mieste už st&aacute;l a Rozgonyiovci ho dali iba prestavať.</p><p>Nesporn&eacute; je, že hrad Čičva plnil počas celej svojej existencie str&aacute;žnu funkciu v priesmyku naz&yacute;vanom &bdquo;Porta Polonica&ldquo;, t. j. Poľsk&aacute; br&aacute;na, na križovatke diaľkov&yacute;ch ciest sp&aacute;jaj&uacute;cich Vranov a Humenn&eacute; a na ceste ved&uacute;cej od S&aacute;rospataku cez Stropkov na sever do Poľska.&ldquo; Podľa Ferdinanda Uličn&eacute;ho bola cesta z Potisia &uacute;dol&iacute;m Tople na sever do Poľska označovan&aacute; už v mlad&scaron;&iacute;ch p&iacute;somn&yacute;ch historick&yacute;ch prameňoch za najd&ocirc;ležitej&scaron;iu obchodn&uacute; uhorsko-poľsk&uacute; cestu v stredoveku. Z&aacute;roveň bol spr&aacute;vnym centrom rovnomenn&eacute;ho feud&aacute;lneho panstva, ku ktor&eacute;mu počas obdobia jeho najv&auml;č&scaron;ej rozlohy patrilo vy&scaron;e 60 ded&iacute;n.</p>";

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
                    if (a.content.length() < 1) {
                        System.out.println("a" + i + "" + j + ".content=\"" + dummyContent.replaceAll("\"", "\\\\\"") + "\";");
                    } else {
                        System.out.println("a" + i + "" + j + ".content=\"" + a.content.replaceAll("\"", "\\\\\"") + "\";");
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
