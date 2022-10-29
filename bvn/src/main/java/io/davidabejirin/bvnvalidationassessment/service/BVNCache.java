package io.davidabejirin.bvnvalidationassessment.service;

import io.davidabejirin.bvnvalidationassessment.payload.BVNInfoDTO;

import java.util.Optional;

public interface BVNCache {

    Optional<BVNInfoDTO> findByBvn(String bvn);
}