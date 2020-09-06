import java.io.*;
import java.util.*;
public class TemuKembaliInformasi {
    public static void main(String[] args) throws IOException {
        Scanner masukan = new Scanner(System.in);
	InputOutputProcess TKI = new InputOutputProcess();
        TKI.bacaMultiFile(new File("SeratusKorpus"));
        TKI.korpusDictionaryConstruct();
        TKI.IDF();
        TKI.bobot_Dok();
        TKI.panjangDok();
        
        System.out.print("\nInput Query: ");
        TKI.simpanKueri(masukan.nextLine());
        TKI.bobotKueri();
        TKI.panjangKueri();
        TKI.CosSim();
    }
    
}
