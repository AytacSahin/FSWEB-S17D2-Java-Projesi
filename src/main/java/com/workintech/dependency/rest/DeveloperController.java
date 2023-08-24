package com.workintech.dependency.rest;

import com.workintech.dependency.mapping.DeveloperResponse;
import com.workintech.dependency.model.Developer;
import com.workintech.dependency.model.JuniorDeveloper;
import com.workintech.dependency.model.MidDeveloper;
import com.workintech.dependency.model.SeniorDeveloper;
import com.workintech.dependency.tax.Taxable;
import com.workintech.dependency.validation.DeveloperValidation;
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
    public DeveloperResponse getById(@PathVariable int id) {
        if (DeveloperValidation.validateId(id)) {
            return new DeveloperResponse(developers.get(id), "Böyle bir id bulunamadı.", 400);
        }
        return new DeveloperResponse(null, "Başarılı.", 200);
    }

    @PostMapping("/")
    public DeveloperResponse createDeveloper(@RequestBody Developer dev) {
        Developer newDeveloper = createDeveloperWithEnum(dev);
        if (newDeveloper == null) {
            return new DeveloperResponse(null, "Experience değerini kontrol et.", 400);
        }
        if (developers.containsKey(dev.getId())) {
            return new DeveloperResponse(null, "Bu id daha önce alınmış.", 400);
        }
        if (!DeveloperValidation.validateDeveloperProperties(dev)) {
            return new DeveloperResponse(null, "Developer bilgilerinde yanlışlık var.", 400);
        }
        developers.put(dev.getId(), newDeveloper);
        return new DeveloperResponse(developers.get(dev.getId()), "Developer successfully created..", 201);
    }

    @PutMapping("/{id}")
    public DeveloperResponse updateDeveloper(@PathVariable int id, @RequestBody Developer dev) {
        if (!developers.containsKey(id)) {
            return new DeveloperResponse(null, "Böyle bir kullanıcı bulunamadı.", 400);
        }
        dev.setId(id);
        Developer updatedDeveloper = createDeveloperWithEnum(dev);
        if (updatedDeveloper == null) {
            return new DeveloperResponse(null, "Experience değerini kontrol et.", 400);
        }
        if (!DeveloperValidation.validateDeveloperProperties(dev)) {
            return new DeveloperResponse(null, "Developer bilgilerinde yanlışlık var.", 400);
        }
        developers.put(id, updatedDeveloper);
        return new DeveloperResponse(updatedDeveloper, "Developer successfully updated..", 200);
    }

    @DeleteMapping("/{id}")
    public DeveloperResponse deleteDeveloper(@PathVariable int id) {
        if (!developers.containsKey(id)) {
            return new DeveloperResponse(null, "Böyle bir kullanıcı bulunamadı.", 400);
        }
        Developer deletedDeveloper = developers.get(id);
        developers.remove(id);
        return new DeveloperResponse(deletedDeveloper, "Developer successfully deleted..", 200);
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
