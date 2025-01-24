package zaidimas;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * Hello world!
 *
 */

public class App {
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.showMenu();

        Characters charactersData = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            charactersData = mapper.readValue(new File(
                    "C:\\Users\\PKC-BRONIIUS\\Desktop\\JavaProjects\\demo\\src\\main\\java\\zaidimas\\data.json"),
                    Characters.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gameplay gameplay = new Gameplay(charactersData);
        gameplay.game();
    }
}
