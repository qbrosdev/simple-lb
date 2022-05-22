package com.qbros.lb.infrastructure;

import com.qbros.lb.core.LoadBalancerImpl;
import com.qbros.lb.core.Provider;
import com.qbros.lb.core.RoundRobinSelection;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller is added to test the random Load balancer in action.
 */
@RestController
@RequestMapping("lb/roundrobin")
public class RoundRobinLBController {

    private final LoadBalancerImpl loadBalancer = LoadBalancerImpl.builder()
            .name("round robin LB")
            .selectionStrategy(new RoundRobinSelection())
            .maxRetryCount(3)
            .build();
    private final Map<String, Provider> providersMap = new HashMap<>();

    public RoundRobinLBController() {

        List<Provider> providerList = List.of(new Provider("RR-P1"), new Provider("RR-P2"), new Provider("RR-P3"),
                new Provider("RR-P4"), new Provider("RR-P5"), new Provider("RR-P6"), new Provider("RR-P7"),
                new Provider("RR-P8"), new Provider("RR-P9"), new Provider("RR-P10"));

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
