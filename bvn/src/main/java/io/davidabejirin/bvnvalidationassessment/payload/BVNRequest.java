package io.davidabejirin.bvnvalidationassessment.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BVNRequest {

    @NotBlank(message = "One or more of your request parameters failed validation. Please retry")
    private String bvn;
}
