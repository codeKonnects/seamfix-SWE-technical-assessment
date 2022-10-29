package io.davidabejirin.bvnvalidationassessment.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "bvn_schema")
@Data
public class BVNRequestResponse {

    @Id
    private UUID id;
    private String request;
    private String response;
}
