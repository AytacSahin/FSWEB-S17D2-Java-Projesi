package com.workintech.dependency.rest;

import com.workintech.dependency.model.Developer;
import com.workintech.dependency.model.JuniorDeveloper;
import com.workintech.dependency.model.MidDeveloper;
import com.workintech.dependency.model.SeniorDeveloper;
import com.workintech.dependency.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController ile controller olduğunu anlattık.
//@RequestMapping ile endpoint izini değiştirdik.

@RestController
@RequestMapping("/developers")
public class DeveloperController {
    private Map<Integer, Developer> developers;
    private Taxable taxable;

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    // Yukarıda tanımladığımız private Taxable taxable; instance variable'ının
    // tanıtılması için dependency injection yapıyoruz.
    // onun hangi inheritance türden olduğunu, bu yaptığımız işlemin ne olduğunu (Autowired) söylüyoruz.
    @Autowired
    public DeveloperController(@Qualifier("developerTax") Taxable taxable) {
        this.taxable = taxable;
    }

    @GetMapping("/")
    public List<Developer> get() {
        return developers.values().stream().toList();
    }
    @GetMapping("/{id}")
    public Developer getById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping("/")
    public Developer createDeveloper(@RequestBody Developer dev) {
        Developer newDeveloper = createDeveloperWithEnum(dev);
        if ( newDeveloper == null) {
            //TODO girdiğiniz bilgileri kontrol ediniz.
        }
        developers.put(dev.getId(), newDeveloper);
        return developers.get(dev.getId());
    }
    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer dev) {
        if(!developers.containsKey(id)) {
            //TODO böyle bir kullanıcı yok...
        }
        dev.setId(id);
        Developer updatedDeveloper = createDeveloperWithEnum(dev);
        developers.put(id, updatedDeveloper);
        return updatedDeveloper;
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        if (!developers.containsKey(id)) {
            //TODO böyle bir kullanıcı yok..
        }
        Developer deletedDeveloper = developers.get(id);
        developers.remove(id);
        return deletedDeveloper;
    }

    public Developer createDeveloperWithEnum(Developer dev) {
        // equalsIgnoreCase metodu büyük küçük harf gözetmeksizin bakar:
        // enum'ın değerini alabilmek için arada .name() koyduk...
        Developer handleDeveloper;
        if (dev.getExperience().name().equalsIgnoreCase("junior")) {
            handleDeveloper =
                    new JuniorDeveloper(dev.getId(),
                            dev.getName(),
                            dev.getSalary() - dev.getSalary() * taxable.getSimpleTaxRate(),
                            dev.getExperience());
        } else if (dev.getExperience().name().equalsIgnoreCase("mid")) {
            handleDeveloper =
                    new MidDeveloper(dev.getId(),
                            dev.getName(),
                            dev.getSalary() - dev.getSalary() * taxable.getMiddleTaxRate(),
                            dev.getExperience());
        } else if (dev.getExperience().name().equalsIgnoreCase("senior")) {
            handleDeveloper =
                    new SeniorDeveloper(dev.getId(),
                            dev.getName(),
                            dev.getSalary() - dev.getSalary() * taxable.getUpperTaxRate(),
                            dev.getExperience());
        } else {
            handleDeveloper = null;
        }
        return handleDeveloper;
    }

}
