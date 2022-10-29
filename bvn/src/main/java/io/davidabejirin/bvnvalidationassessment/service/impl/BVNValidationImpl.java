package io.davidabejirin.bvnvalidationassessment.service.impl;

import io.davidabejirin.bvnvalidationassessment.util.ResponseStatusCode;
import io.davidabejirin.bvnvalidationassessment.payload.BVNInfoDTO;
import io.davidabejirin.bvnvalidationassessment.payload.BVNRequest;
import io.davidabejirin.bvnvalidationassessment.payload.BVNResponse;
import io.davidabejirin.bvnvalidationassessment.service.BVNCache;
import io.davidabejirin.bvnvalidationassessment.service.BVNValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BVNValidationImpl implements BVNValidation {

    private final BVNCache BVNCache;

    @Override
    public ResponseEntity<BVNResponse> validateBvn(BVNRequest request) {
        String bvn = request.getBvn();
        if (!hasValidNumberOfDigit(bvn)) {
            return new ResponseEntity<>(getInvalidBVNDigitsCount(bvn), HttpStatus.BAD_REQUEST);
        }
        if (!hasOnlyDigits(bvn)) {
            return new ResponseEntity<>(getInvalidBVNWithNonDigits(bvn), HttpStatus.BAD_REQUEST);
        }

        Optional<BVNInfoDTO> optionalBvnDetails = BVNCache.findByBvn(bvn);
        if (optionalBvnDetails.isEmpty()) {
            return new ResponseEntity<>(getBVNNotFound(bvn), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(getBVNSuccess(optionalBvnDetails.get()), HttpStatus.OK);
    }

    private boolean hasValidNumberOfDigit(String bvn) {
        int validDigitCount = 11;
        return bvn.length() == validDigitCount;
    }

    private BVNResponse getInvalidBVNDigitsCount(String bvn) {
        return BVNResponse.builder()
                .code(ResponseStatusCode.INVALID_BVN_DIGITS)
                .message("The searched BVN is invalid")
                .bvn(bvn)
                .build();
    }

    private boolean hasOnlyDigits(String bvn) {
        for (int i = 0; i < bvn.length(); i++) {
            char currentChar = bvn.charAt(i);
            boolean isNumber = currentChar >= '0' && currentChar <= '9';
            if (!isNumber)
                return false;
        }
        return true;
    }

    private BVNResponse getInvalidBVNWithNonDigits(String bvn) {
        return BVNResponse.builder()
                .code(ResponseStatusCode.BAD_REQUEST)
                .message("The searched BVN is invalid")
                .bvn(bvn)
                .build();
    }

    private BVNResponse getBVNNotFound(String bvn) {
        return BVNResponse.builder()
                .code(ResponseStatusCode.NOT_FOUND)
                .message("The searched BVN does not exist")
                .bvn(bvn)
                .build();
    }

    private BVNResponse getBVNSuccess(BVNInfoDTO BVNInfoDTO) {
        return BVNResponse.builder()
                .code(ResponseStatusCode.SUCCESS)
                .message("SUCCESS")
                .bvn(BVNInfoDTO.getBvn())
                .basicDetail(BVNInfoDTO.getBasicDetail())
                .imageDetail(BVNInfoDTO.getImageDetail())
                .build();
    }
}
