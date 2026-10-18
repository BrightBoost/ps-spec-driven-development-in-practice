package com.example.obligations.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.obligations.domain.ObligationService;
import com.example.obligations.domain.ObligationView;

@RestController
@RequestMapping("/obligations")
public class ObligationController {

    private final ObligationService service;

    public ObligationController(ObligationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ObligationResponse> create(@RequestBody CreateObligationRequest request) {
        ObligationView created = service.create(request.dueDate(), request.ownerEmail(), request.complianceLeadEmail());
        return ResponseEntity
                .created(URI.create("/obligations/" + created.obligation().id()))
                .body(ObligationResponse.from(created));
    }

    @GetMapping("/due-soon")
    public List<ObligationResponse> dueSoon() {
        return service.dueSoon().stream().map(ObligationResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ObligationResponse get(@PathVariable String id) {
        return ObligationResponse.from(service.get(id));
    }
}
