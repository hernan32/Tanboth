import java.io.IOException;

public class Tanboth {

    public static void main(String[] args) throws IOException, InterruptedException {

        GameParser flashClient = new GameParser("Knobbers", "35413880");

        //Tanoth Testing

       /* System.out.println("##############QUEST INFO##############");
        for (Adventure adventure : flashClient.getAdventures()) {
            System.out.println("[QUEST]");
            System.out.println("Dificultad: "+adventure.getDifficulty());
            System.out.println("Duracion: "+adventure.getDuration());
            System.out.println("Exp: "+adventure.getExperience());
            System.out.println("Chance: "+adventure.getFightChance());
            System.out.println("Oro: "+adventure.getGold());
            System.out.println("Quest ID: "+adventure.getQuestID());
        }*/
        System.out.println("##############QUESTS INFO##############");
        String cantidad;
        try {
            cantidad = Integer.toString(flashClient.getAdventures().size());
        } catch (AdventureRunningException ex) {
            cantidad = ex.getMessage();
        }
        System.out.println("Cantidad Quest: " + cantidad);

        System.out.println("############INVENTORY INFO############");
        System.out.println("Espacios en el Inventario: " + flashClient.getInventorySpace());


    }
}
