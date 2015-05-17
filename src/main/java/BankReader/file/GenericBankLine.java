
package BankReader.file;

import BankReader.category.Category;
import BankReader.category.FinancialCategory;
import BankReader.category.SubCategory;
import BankReader.file.ABN.ABNBankLine;
import BankReader.util.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

/**
 * Created by jan on 2-5-15.
 */
public class GenericBankLine {

    private Category category;
    private SubCategory subCategory;
    private Date date;
    private Amount amount;
    private String description;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }
}
