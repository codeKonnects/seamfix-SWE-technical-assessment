package io.davidabejirin.bvnvalidationassessment.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BVNResponse {

    private String message;
    private String code;
    private String bvn;
    private String imageDetail;
    private String basicDetail;
}
