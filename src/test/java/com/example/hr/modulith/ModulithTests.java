package com.example.hr.modulith;

import com.example.hr.HrApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithTests {
    @Test
    void verifiesModuleStructure() {
        ApplicationModules.of(HrApiApplication.class).verify();
    }
}
