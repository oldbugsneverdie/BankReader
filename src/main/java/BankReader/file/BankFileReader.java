package BankReader.file;

import BankReader.file.ABN.ABNBankLine;
import BankReader.file.ABN.ABNFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 3-5-15.
 */
public class BankFileReader {

    public static final String ABN_PREFIX = "ABN";

    private List<ABNBankLine> bankLines = new ArrayList<ABNBankLine>();

    public void process(File file) {

        if (file.getName().startsWith(ABN_PREFIX)){
            ABNFileReader abnFileReader = new ABNFileReader();
            bankLines.addAll(abnFileReader.readFile(file));
        } else {
            return;
        }


    }
}
