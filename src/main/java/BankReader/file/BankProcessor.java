package BankReader.file;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by jadu on 10-5-2015.
 */
public class BankProcessor{


    @Value("${input.directory}")
    protected String inputDirectory;


}
