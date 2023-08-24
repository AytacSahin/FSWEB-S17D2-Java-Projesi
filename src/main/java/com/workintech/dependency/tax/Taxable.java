package com.workintech.dependency.tax;

public interface Taxable {
    double getSimpleTaxRate();
    double getMiddleTaxRate();
    double getUpperTaxRate();
}
