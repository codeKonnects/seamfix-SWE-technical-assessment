package io.davidabejirin.bvnvalidationassessment.repository;

import io.davidabejirin.bvnvalidationassessment.entity.BVNRequestResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BVNRequestResponseRepository extends MongoRepository<BVNRequestResponse, UUID> {
}
