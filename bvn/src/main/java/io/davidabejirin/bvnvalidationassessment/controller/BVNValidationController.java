package io.davidabejirin.bvnvalidationassessment.controller;

import io.davidabejirin.bvnvalidationassessment.payload.BVNRequest;
import io.davidabejirin.bvnvalidationassessment.payload.BVNResponse;
import io.davidabejirin.bvnvalidationassessment.service.BVNValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class BVNValidationController {

    private final BVNValidation BVNValidation;


    @PostMapping("/bv-service/svalidate/wrapper")
    public ResponseEntity<BVNResponse> validateBvn(@Valid @RequestBody BVNRequest request) {
        return BVNValidation.validateBvn(request);
    }
}
