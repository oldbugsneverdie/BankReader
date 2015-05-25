
package BankReader.file;

import BankReader.category.Category;
import BankReader.category.SubCategory;
import BankReader.util.Amount;

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

    public GenericBankLine() {
    }

    public GenericBankLine(GenericBankLine genericBankLine) {
        this.category = genericBankLine.getCategory();
        this.subCategory = genericBankLine.getSubCategory();
        this.date = genericBankLine.getDate();
        this.amount = genericBankLine.getAmount();
        this.description = genericBankLine.getDescription();
    }

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

    @Override
    public String toString() {
        String categoryName = (this.category == null) ? "?" : this.category.getName();
        String subCategoryName = (this.subCategory == null) ? "?" : this.subCategory.getName();
        String date = (this.date == null) ? "?" : this.date.toString();
        String amount = (this.amount == null) ? "?" : this.amount.toString();
        String description = (this.description == null) ? "?" : this.description;

        return "Category " + categoryName
                + ", subcategory " + subCategoryName
                + ", date " + date
                + ", amount " + amount
                + ", description " + description;

    }
}
