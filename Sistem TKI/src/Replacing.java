import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replacing {
    
    public static boolean cekKata(String pola, String kata){
        Pattern p = Pattern.compile(pola);
        Matcher m = p.matcher(kata);
        return m.find();
    }
    
    public String gantiKataGaul(String kata){
        if (cekKata("^(g|ga|gk|gak|gx|ngga|nggak|enggak|tdk)$", kata)) {
            kata = "tidak";
        }
//        else if (cekKata("^()$", kata)) {
//            kata = "";
//        }
//        else if (cekKata("^()$", kata)) {
//            kata = "";
//        }
        return kata;
    }
    
}
