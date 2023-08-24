package com.workintech.dependency.tax;

import org.springframework.stereotype.Component;

@Component
public class DeveloperTax implements Taxable{
    @Override
    public double getSimpleTaxRate() {
        return 0.25;
    }

    @Override
    public double getMiddleTaxRate() {
        return 0.30;
    }

    @Override
    public double getUpperTaxRate() {
        return 0.4;
    }
}
