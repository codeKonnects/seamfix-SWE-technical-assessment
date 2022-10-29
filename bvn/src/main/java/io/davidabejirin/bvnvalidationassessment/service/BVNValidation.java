package io.davidabejirin.bvnvalidationassessment.service;

import io.davidabejirin.bvnvalidationassessment.payload.BVNRequest;
import io.davidabejirin.bvnvalidationassessment.payload.BVNResponse;
import org.springframework.http.ResponseEntity;

public interface BVNValidation {

    ResponseEntity<BVNResponse> validateBvn(BVNRequest request);
}
