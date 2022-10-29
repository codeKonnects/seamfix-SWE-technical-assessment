package io.davidabejirin.bvnvalidationassessment.util;

import io.davidabejirin.bvnvalidationassessment.entity.BVNRequestResponse;
import io.davidabejirin.bvnvalidationassessment.repository.BVNRequestResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private final BVNRequestResponseRepository BVNRequestResponseRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);

        String requestPayload = new String(
                contentCachingRequestWrapper.getContentAsByteArray(),
                contentCachingRequestWrapper.getCharacterEncoding()
        );

        String responsePayload = new String(
                contentCachingResponseWrapper.getContentAsByteArray(),
                contentCachingResponseWrapper.getCharacterEncoding()
        );

        savePayloadAsync(requestPayload, responsePayload);

        contentCachingResponseWrapper.copyBodyToResponse();
    }

    private void savePayloadAsync(String requestPayload, String responsePayload) {
        CompletableFuture.runAsync(() -> {
            BVNRequestResponse entry = new BVNRequestResponse();
            entry.setId(UUID.randomUUID());
            entry.setRequest(requestPayload);
            entry.setResponse(responsePayload);

            BVNRequestResponseRepository.save(entry);
        });
    }
}
