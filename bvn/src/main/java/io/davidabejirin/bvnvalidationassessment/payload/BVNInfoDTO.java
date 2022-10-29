package io.davidabejirin.bvnvalidationassessment.payload;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BVNInfoDTO {

    private long id;

    private String bvn;

    private String imageDetail;

    private String basicDetail;
}
