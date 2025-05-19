import Service.CatalogService;
import Service.UIService;

/**
 * Clasa principală care pornește aplicația Catalog Electronic
 */
public class Main {
    
    /**
     * Metoda main - punctul de intrare în aplicație
     * @param args Argumentele liniei de comandă
     */
    public static void main(String[] args) {
        // Inițializăm serviciile
        CatalogService catalogService = new CatalogService();
        UIService uiService = new UIService(catalogService);
        
        // Register a shutdown hook to ensure proper cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing application and saving all data...");
            // All services have their own shutdown hooks to handle connection closing
        }));

        // Bucla principală a aplicației
        boolean continuaAplicatia = true;
        while (continuaAplicatia) {
            // Afișează meniul și așteaptă input de la utilizator
            uiService.afiseazaMeniuPrincipal();
            
            try {
                // Citim opțiunea și tratăm eventualele erori de format
                int optiune = new java.util.Scanner(System.in).nextInt();
                
                // Procesăm opțiunea aleasă
                continuaAplicatia = uiService.trateazaOptiune(optiune);
            } 
            catch (NumberFormatException e) {
                System.out.println("Eroare: Introduceți un număr valid!");
            }
            catch (Exception e) {
                System.out.println("Eroare neașteptată: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}