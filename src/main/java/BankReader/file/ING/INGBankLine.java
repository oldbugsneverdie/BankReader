package BankReader.file.ING;

/**
 * Created by jan on 2-5-15.
 */
public class INGBankLine {

    private String datum;
    private String omschrijving;
    private String rekening;
    private String tegenRekening;
    private String code;
    private String afBij;
    private String bedrag;
    private String mutatieSoort;
    private String mededelingen;

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public String getTegenRekening() {
        return tegenRekening;
    }

    public void setTegenRekening(String tegenRekening) {
        this.tegenRekening = tegenRekening;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAfBij() {
        return afBij;
    }

    public void setAfBij(String afBij) {
        this.afBij = afBij;
    }

    public String getBedrag() {
        return bedrag;
    }

    public void setBedrag(String bedrag) {
        this.bedrag = bedrag;
    }

    public String getMutatieSoort() {
        return mutatieSoort;
    }

    public void setMutatieSoort(String mutatieSoort) {
        this.mutatieSoort = mutatieSoort;
    }

    public String getMededelingen() {
        return mededelingen;
    }

    public void setMededelingen(String mededelingen) {
        this.mededelingen = mededelingen;
    }
}
