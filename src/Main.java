import br.edu.ahtl.database.LibraryRepository;
import br.edu.ahtl.interfaces.Library;
import br.edu.ahtl.services.LibraryService;
import br.edu.ahtl.views.ComandLineInterface;

public class Main {
    public static void main(String[] args) {
        Library libraryService = new LibraryService(LibraryRepository.getInstance());
        ComandLineInterface cli = new ComandLineInterface(libraryService);
        
        cli.main();
    }
}