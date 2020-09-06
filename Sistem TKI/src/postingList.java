import java.util.ArrayList;
public class postingList {
    ArrayList<Integer> indexDok;
    ArrayList<Integer> frekKueri;
    double idf;
    ArrayList<String> namaDok;
    ArrayList<ArrayList<Integer>> frekTermDiDok;
	
    public postingList(String nameDoc, int posisiTerm, int ind) {
	namaDok = new ArrayList<String>();
	ArrayList<Integer> posisi = new ArrayList<Integer>();
	frekTermDiDok = new ArrayList<ArrayList<Integer>>();
        indexDok = new ArrayList<Integer>();
        frekKueri = new ArrayList<Integer>();
        namaDok.add(nameDoc);
        posisi.add(posisiTerm);
        frekTermDiDok.add(posisi);
        indexDok.add(ind);
    }

    public postingList(int posisiKueri) {
        frekKueri.add(posisiKueri);
    }

}