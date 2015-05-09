package BankReader.file.ABN;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 2-5-15.
 */
public class ABNFileReader {

    @Autowired
    private FlatFileItemReader<ABNBankLine> reader;

    public ABNFileReader() {
    }

    public List<ABNBankLine> readFile(File file) {

        //http://www.concretepage.com/spring-batch-3/spring-batch-3-flatfileitemreader-flatfileitemwriter-annotation-example
        //reader.
        return new ArrayList<ABNBankLine>();
    }

}
