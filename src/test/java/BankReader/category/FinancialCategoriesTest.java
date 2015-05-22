package BankReader.category;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by jadu on 22-5-2015.
 */
public class FinancialCategoriesTest {

    @Test
    public void testCategories(){

        // Setup
        FinancialCategories financialCategories = new FinancialCategories();
        financialCategories.addFinancialCategory("akgul market", "huishouden", "super");

        // Test
        SubCategory subCategory = financialCategories.getSubCategory("BEA   NR:4KSN3F   10.01.15/14.39 Akgul Market HAARLEM");

        // Verify
        assertNotNull(subCategory);


    }
}
