import java.io.IOException;

public class Tanboth {

    public static void main(String[] args) throws IOException, InterruptedException {

        FlashModel flashClient = new FlashModel("Knobbers","35413880");

        //Tanoth Flash Model
        System.out.println("<---------------------->");
        for (Adventure adventure : flashClient.getAdventures()) {
            System.out.println("~~ QUEST ~~");
            System.out.println("Dificultad: "+adventure.getDifficulty());
            System.out.println("Duracion: "+adventure.getDuration());
            System.out.println("Exp: "+adventure.getExperience());
            System.out.println("Chance: "+adventure.getFightChance());
            System.out.println("Oro: "+adventure.getGold());
            System.out.println("Quest ID: "+adventure.getQuestID());
        }
        System.out.println("<---------------------->");
        System.out.println("Cantidad Quest: "+flashClient.getAdventures().size());

    }
}
