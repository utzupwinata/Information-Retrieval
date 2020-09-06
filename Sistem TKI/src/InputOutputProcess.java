import java.io.*;
import java.util.*;

public class InputOutputProcess {
    ArrayList<String> isiFile;
    String spoiler[];
    int spoiler_counter;
    File file[];
    int JumlahDok;
//______________________________________________________________________________
    public void bacaMultiFile(File berkas) throws IOException {
        file = berkas.listFiles();
        JumlahDok = file.length;
        isiFile = new ArrayList<String>();
        spoiler = new String[file.length];
        for(int i=0; i<file.length; i++) {
            BufferedReader br = new BufferedReader(new FileReader(file[i]));
            //System.out.println(file[i].getAbsolutePath());            
            Scanner baca = new Scanner(br);
            String isi="";
            while(baca.hasNextLine()) {
                isi = isi + baca.nextLine().toLowerCase().replaceAll("[^A-Za-z\']", " ").trim();
            }
            spoiler[i] = isi.substring(0,70)+"...";
            isiFile.add(isi);
            baca.close();
        }
    }
//______________________________________________________________________________
    ArrayList<String> stoplist;
	
    public boolean stopWordRemoval(String kt) {
	stoplist = new ArrayList<String>();
	try {
            BufferedReader br = new BufferedReader(new FileReader("stopwordlist.txt"));
            Scanner baca = new Scanner(br);
            String kata = "";
            while (baca.hasNext()) {
                kata = baca.next().toLowerCase();
                stoplist.add(kata);
            }
           baca.close();
        }
        catch (Exception e) {
            System.out.println("File stopword tidak ada!");
        }
        return (stoplist.contains(kt));
    }
//______________________________________________________________________________
    HashMap<String, postingList> daftarTerm = new HashMap<String, postingList>();
    postingList postList;
    public void invertedIndex(String term, String namaDok, int posisi, int indexDok) {
	if(daftarTerm.containsKey(term)) {
            postList = daftarTerm.get(term);
            if(postList.namaDok.indexOf(namaDok)<0) {
		postList.namaDok.add(namaDok);
		postList.frekTermDiDok.add(new ArrayList<Integer>());                       
                postList.indexDok.add(indexDok);//tambahan
            }
            postList.frekTermDiDok.get(postList.namaDok.indexOf(namaDok)).add(posisi);
            daftarTerm.put(term, postList);
	}
        else {
            daftarTerm.put(term, new postingList(namaDok, posisi, indexDok));
	}
    }
//______________________________________________________________________________
    String before_stemming;
    StemmingProcess Stem = new StemmingProcess();
    
    public void korpusDictionaryConstruct() {
	String isiDok, token;
	StringTokenizer st;
	for(int i=0; i<isiFile.size(); i++) {
            System.out.println("\nLoading File " + (i+1) + "...");
            isiDok = isiFile.get(i);
            st = new StringTokenizer(isiDok);
            int posisi = 1;
            while(st.hasMoreTokens()) {//Jika korpus terdiri dari >1 kata, pecah kata tersebut satu per satu
                token = st.nextToken();
                if(!stopWordRemoval(token)){//---------------------------------- stopword list
                    Stem.InsertKamus();
                    before_stemming = token;
                    Stem.Cegah_Perulangan_Rule();//----------------------------- algoritma stemming
                    token = Stem.CS_Stemmer(token);
                    Stem.Cegah_Perulangan_Rule();
                    token = Stem.CS_Stemmer2(token);
                    /*Stem.Cegah_Perulangan_Rule();
                    token = Stem.Stemmer_NA(token);
                    Stem.Cegah_Perulangan_Rule();
                    token = Stem.Stemmer_NA2(token);*/
                    invertedIndex(token, "Dok-"+(i+1), posisi, i);//------------ buat inverted index (indexing)
                    //System.out.println(before_stemming+" // "+token);
                    posisi++;
                }
            }
        System.out.println("File " +(i+1) + " Loaded");
        }
    }
//______________________________________________________________________________
    public void IDF(){
        double temp_x;
        
        for(Map.Entry<String, postingList> entry: daftarTerm.entrySet()){ 
            postList = daftarTerm.get(entry.getKey());
            temp_x = JumlahDok/entry.getValue().namaDok.size();//--------------- rumus hitung IDF 1.1
            postList.idf = Math.log10(temp_x); //------------------------------- rumus hitung IDF 1.2
            daftarTerm.put(entry.getKey(), postList);
        }
    }
//______________________________________________________________________________
    double bobotDok[][];
    //Menghitung bobot semua dokumen yang dipunya (TD-IDF)
    public void bobot_Dok(){
        bobotDok = new double[daftarTerm.size()][JumlahDok];
        int a = 0;
        
        for(Map.Entry<String, postingList> entry: daftarTerm.entrySet()){ //---- looping HashMAp per Term
            for(int i=0; i<entry.getValue().namaDok.size(); i++){//------------- looping sebanyak dokumen (korpus)
                bobotDok[a][entry.getValue().indexDok.get(i)]=
                        entry.getValue().frekTermDiDok.get(i).size() * entry.getValue().idf; // rumus TF*IDF
            }
            a++;
        }
    }
//______________________________________________________________________________
    double[] panjangDok;
    //Menghitung Panjang Setiap Dokumen menggunakan Rumus CosSim
    public void panjangDok(){
        panjangDok = new double[JumlahDok];
        double sum[] = new double[JumlahDok];
        
        for(int i=0; i<JumlahDok; i++){//--------------------------------------- looping sebanyak dokumen
            for(int j=0; j<bobotDok.length; j++){//----------------------------- looping sebanyak term-nya, j=size Term
                sum[i] += (bobotDok[j][i] * bobotDok[j][i]); //Sum(bobot[1] kuadrat + bobot[2] kuadrat + bobot[n] kuadrat + ...)
            }
        }
        for(int i=0; i<sum.length; i++){
            panjangDok[i] = Math.sqrt(sum[i]);//-------------------------------- Akarkan hasil Sum, sesuai rumus CosSim
        }
    }
//______________________________________________________________________________
    //Jika kueri ada di HashMap/Indexing dokumen maka buat inverted index untuk term kueri tsb
    public void invertedIndexKueri(String term, int posKueri){
        if(daftarTerm.containsKey(term)){
            postList = daftarTerm.get(term);
            postList.frekKueri.add(posKueri);
            daftarTerm.put(term, postList);
        }
    }
//______________________________________________________________________________
    String termKueri;
    
    public void simpanKueri(String kueri){
        StringTokenizer st = new StringTokenizer(kueri);
        int posisiKueri = 1;
        
        while(st.hasMoreTokens()){ //Jika kueri terdiri dari >1 kata, pecah kata tersebut satu per satu
            termKueri = st.nextToken().toLowerCase();
            
            if(!stopWordRemoval(termKueri)){//---------------------------------- stopword list
                Stem.InsertKamus();
                Stem.Cegah_Perulangan_Rule();
                termKueri = Stem.CS_Stemmer(termKueri);//----------------------- algoritma stemming
                Stem.Cegah_Perulangan_Rule();
                termKueri = Stem.CS_Stemmer2(termKueri);
                Stem.Cegah_Perulangan_Rule();
                termKueri = Stem.Stemmer_NA(termKueri);
                Stem.Cegah_Perulangan_Rule();
                termKueri = Stem.Stemmer_NA2(termKueri);
                invertedIndexKueri(termKueri, posisiKueri);//------------------- buat inverted index term kueri
                posisiKueri++;
            }
        }
    }
//______________________________________________________________________________
    double[] bobotKueri;
    //Menghitung bobot term dalam kueri (TF kata dalam Kueri * IDF korpus/dokumen)
    public void bobotKueri(){
        bobotKueri = new double[daftarTerm.size()];
        int a = 0;
        for(Map.Entry<String, postingList> entry: daftarTerm.entrySet()){
            bobotKueri[a] = entry.getValue().frekKueri.size() * daftarTerm.get(entry.getKey()).idf; // Dikalikan dengan IDF korpus
            a++;
        }
    }
//______________________________________________________________________________
    double panjangKueri;
    //Menghitung panjang term dalam kueri
    public void panjangKueri(){
        double sumKueri = 0;
        
        for(int i=0; i<bobotKueri.length; i++){
            sumKueri += (bobotKueri[i] * bobotKueri[i]);
        }
        panjangKueri = Math.sqrt(sumKueri);
    }
//______________________________________________________________________________
    double similarity[];
    //Menghitung similarity korpus terhadap kueri/sebaliknya
    public void CosSim(){
        similarity = new double[JumlahDok];
        double sum[] = new double[JumlahDok];
        for(int i=0; i<JumlahDok; i++){
            for(int j=0; j<bobotDok.length; j++){
                sum[i] += (bobotDok[j][i] * bobotKueri[j]); //Sum(Bobot Dokumen ke-n * Bobot Kueri)
            }
        }
        System.out.println();
        ArrayList<SortingSimiliarity> arraylist = new ArrayList<>();
        for(int i=0; i<similarity.length; i++){
            similarity[i] = sum[i]/(panjangDok[i]*panjangKueri); //------------- rumus CosSim (Vector Space Model)
            //System.out.println("Similarity Dok("+(i+1)+") dengan Kueri = "+similarity[i]);
            arraylist.add(new SortingSimiliarity((i+1),similarity[i],spoiler[i]));//------- sorting hasil similiarity tiap korpus
        }
        System.out.println();
        arraylist.sort(Comparator.comparingDouble(SortingSimiliarity::getSimilarity).reversed());
        arraylist.forEach(System.out::println);
        //arraylist.clear();
    }
//______________________________________________________________________________
    public void cobaCetak() {
	for(Map.Entry<String, postingList> entry : daftarTerm.entrySet()) {
		System.out.print(entry.getKey()+" : ");
		for(int i=0; i<entry.getValue().namaDok.size(); i++) {
			System.out.print(entry.getValue().namaDok.get(i)+"");
			System.out.print("(tf="+entry.getValue().frekTermDiDok.get(i).size()+" : "
					+entry.getValue().frekTermDiDok.get(i)+")");
			System.out.print(",");
		}
		System.out.println();
	}
    }
//______________________________________________________________________________
}
