package BankReader.file.ABN;

import org.springframework.batch.item.ItemProcessor;

/**
 * Created by jan on 2-5-15.
 */
public class ABNBankLine {

    private String rekeningnummer;
    private String Muntsoort;
    private String Transactiedatum;
    private String Rentedatum;
    private String Beginsaldo;
    private String Eindsaldo;
    private String Transactiebedrag;
    private String Omschrijving;

    public String getRekeningnummer() {
        return rekeningnummer;
    }

    public void setRekeningnummer(String rekeningnummer) {
        this.rekeningnummer = rekeningnummer;
    }

    public String getMuntsoort() {
        return Muntsoort;
    }

    public void setMuntsoort(String muntsoort) {
        Muntsoort = muntsoort;
    }

    public String getTransactiedatum() {
        return Transactiedatum;
    }

    public void setTransactiedatum(String transactiedatum) {
        Transactiedatum = transactiedatum;
    }

    public String getRentedatum() {
        return Rentedatum;
    }

    public void setRentedatum(String rentedatum) {
        Rentedatum = rentedatum;
    }

    public String getBeginsaldo() {
        return Beginsaldo;
    }

    public void setBeginsaldo(String beginsaldo) {
        Beginsaldo = beginsaldo;
    }

    public String getEindsaldo() {
        return Eindsaldo;
    }

    public void setEindsaldo(String eindsaldo) {
        Eindsaldo = eindsaldo;
    }

    public String getTransactiebedrag() {
        return Transactiebedrag;
    }

    public void setTransactiebedrag(String transactiebedrag) {
        Transactiebedrag = transactiebedrag;
    }

    public String getOmschrijving() {
        return Omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        Omschrijving = omschrijving;
    }


}
