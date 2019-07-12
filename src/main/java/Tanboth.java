import java.io.IOException;

public class Tanboth {

    public static void main(String[] args) throws IOException, InterruptedException {

        GameParser flashClient = new GameParser("Knobbers", "35413880");

        //TANOTH TESTING

        System.out.println("##############QUESTS INFO##############");
        try {
            System.out.println("Aventuras de Hoy: " + flashClient.getAdventuresMadeToday() + " / " + flashClient.getFreeAdventuresPerDay());
        } catch (AdventureRunningException ex) {
            System.out.println(ex.getMessage());
        }


        System.out.println("############INVENTORY INFO############");
        System.out.println("Espacios en el Inventario: " + flashClient.getInventorySpace());

        System.out.println("##########QUEST RUNNING INFO##########");
        try {
            flashClient.getAdventures();
            System.out.println("[INFO] No Active Quest - Starting Now");
            Adventure activeAdventure = flashClient.startAdventureByCriteria("EXP");
            System.out.print("[QUEST]: ");
            System.out.print("Dificultad: " + activeAdventure.getDifficulty() + " / ");
            System.out.print("Duracion: " + activeAdventure.getDuration() + " / ");
            System.out.print("Exp: " + activeAdventure.getExperience() + " / ");
            System.out.print("Chance: " + activeAdventure.getFightChance() + " / ");
            System.out.print("Oro: " + activeAdventure.getGold() + " / ");
            System.out.println("Quest ID: " + activeAdventure.getQuestID());
        } catch (AdventureRunningException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("#####################################");


    }
}
