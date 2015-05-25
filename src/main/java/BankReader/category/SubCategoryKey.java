package BankReader.category;

import BankReader.file.GenericBankLine;
import BankReader.util.Amount;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * The part of a bank line description that identifies the sub category.
 */
public class SubCategoryKey{

    private String key;
    private String comment = null;

    public SubCategoryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        String result = "Key: '" + this.key + "'";
        if (comment !=null){
            result += ". Comment: " + comment;
        }
        return result;
    }
}
