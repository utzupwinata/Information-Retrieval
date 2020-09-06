public class SortingSimiliarity {
    int nomor_korpus;
    double similarity;
    String isi_korpus;
            
    public SortingSimiliarity(int nomor_korpus, double similarity, String isi_kopus){
        this.nomor_korpus = nomor_korpus;
        this.similarity   = similarity;
        this.isi_korpus   = isi_kopus;
    }
    
    public int getNomor_korpus(){
        return nomor_korpus;
    }
    
    public void setNomor_korpus(int nomor_korpus){
	this.nomor_korpus = nomor_korpus;
    }
    
    public double getSimilarity(){
        return similarity;
    }
    
    public void setSimilarity(double similarity){
	this.similarity = similarity;
    }
    
    public String getIsi_kopus(){
        return isi_korpus;
    }
    
    public void setIsi_korpus(String isi_korpus){
	this.isi_korpus = isi_korpus;
    }
    
    @Override
    public String toString() {
        return "[ Nomor Korpus = " + nomor_korpus + ",\t Similarity = " + similarity + ",\t Isi Korpus = "+ isi_korpus +"]";
    }
}
