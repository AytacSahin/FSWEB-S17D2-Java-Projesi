package com.workintech.dependency.validation;

import com.workintech.dependency.mapping.DeveloperResponse;
import com.workintech.dependency.model.Developer;

public class DeveloperValidation {
    public static boolean validateId(int id) {
        return id > 0;
    }

    public static boolean validateDeveloperProperties(Developer developer) {
       return validateId(developer.getId()) &&
               developer.getName() != null &&
               !developer.getName().isEmpty() &&
               developer.getSalary() > 14.500;
    }
}
