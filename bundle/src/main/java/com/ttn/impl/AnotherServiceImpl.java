package com.ttn.impl;

import javax.jcr.Repository;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;

import com.ttn.*;

@Component(label = "Anorther Serice Project", metatype = true, immediate = true, createPid = true)
@Properties(value = { @Property(name = "service.description", value = "Test another service"),
                      @Property(name = "service.tmp", value = "/tmp", label = "Temporary Property", description = "just to check referencing") })

@Service
public class AnotherServiceImpl implements AnotherService {
    
    @Reference
    private HelloService helloService;

    public String getFirstService() {
        return helloService.getName();
    }

}
