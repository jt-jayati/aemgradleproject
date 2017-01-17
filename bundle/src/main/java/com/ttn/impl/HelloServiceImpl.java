package com.ttn.impl;

import javax.jcr.Repository;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;

import com.ttn.HelloService;

/**
 * One implementation of the {@link HelloService}. Note that
 * the repository is injected, not retrieved.
 */

@Component(label = "Test Project", metatype = true, immediate = true, createPid = true)
@Properties(value = { @Property(name = "service.description", value = "Test service"),
                      @Property(name = "service.tmp", value = "/tmp", label = "Temporary Property", description = "just to check metatype") })

@Service
public class HelloServiceImpl implements HelloService {

    @Reference
    private SlingRepository repository;

    public String getName() {
        return repository.getDescriptor(Repository.REP_NAME_DESC);
    }

}
