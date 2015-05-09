package BankReader.directory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;

/**
 * Created by jan on 3-5-15.
 */
public abstract class DirectoryReader {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryReader.class);

    public void process(File directory) throws IOException {
        Assert.isTrue(directory.isDirectory());

        FileReadingMessageSource fileReadingMessageSource = new FileReadingMessageSource();
        fileReadingMessageSource.setDirectory(directory);
        fileReadingMessageSource.setAutoCreateDirectory(true);

        boolean go = true;
        while(go){
            Message<File> message = fileReadingMessageSource.receive();
            if (message==null){
                go = false;
                break;
            }
            File file = message.getPayload();
            if (file.isFile()){
                LOG.info("Found file: {}", file.getCanonicalPath());
                handleFile(file);
            }
        }

    }

    protected abstract void handleFile(File file);


}
