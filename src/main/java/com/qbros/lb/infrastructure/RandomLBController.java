package com.qbros.lb.infrastructure;

import com.qbros.lb.core.LoadBalancerImpl;
import com.qbros.lb.core.Provider;
import com.qbros.lb.core.RandomSelection;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller is added to test the random Load balancer in action.
 */
@RestController
@RequestMapping("lb/random")
public class RandomLBController {

    private final LoadBalancerImpl loadBalancer = LoadBalancerImpl.builder()
            .name("random LB")
            .selectionStrategy(new RandomSelection())
            .maxRetryCount(3)
            .build();

    private final Map<String, Provider> providersMap = new HashMap<>();

    public RandomLBController() {
        List<Provider> providerList = List.of(new Provider("P1"), new Provider("P2"), new Provider("P3"),
                new Provider("P4"), new Provider("P5"), new Provider("P6"), new Provider("P7"),
                new Provider("P8"), new Provider("P9"), new Provider("P10"));

        providerList.forEach(provider -> providersMap.put(provider.getId(), provider));
        loadBalancer.registerAll(providerList);
    }

    @GetMapping
    public String provide() {
        return loadBalancer.get();
    }

    @PostMapping("exclude/{id}")
    public void excludeById(@PathVariable String id) {
        loadBalancer.exclude(providersMap.get(id.toUpperCase()));
    }

    @PostMapping("include/{id}")
    public void includeById(@PathVariable String id) {
        loadBalancer.include(providersMap.get(id.toUpperCase()));
    }
}
