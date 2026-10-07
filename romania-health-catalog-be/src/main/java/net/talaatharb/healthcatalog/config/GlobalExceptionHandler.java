package net.talaatharb.healthcatalog.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.config.UploadSecretVerifier.InvalidUploadSecretException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(net.talaatharb.healthcatalog.service.CatalogUrlImporter.DownloadException.class)
	public ProblemDetail handleDownloadException(net.talaatharb.healthcatalog.service.CatalogUrlImporter.DownloadException ex) {
		log.warn("Catalog URL import failed: {}", ex.getMessage());
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
		problem.setTitle("Catalog download failed");
		return problem;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
		log.error("IllegalArgumentException: {}", ex.getMessage());
		
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.BAD_REQUEST, 
			ex.getMessage()
		);
		problemDetail.setTitle("Invalid Request");
		
		return problemDetail;
	}

	@ExceptionHandler(InvalidUploadSecretException.class)
	public ProblemDetail handleInvalidUploadSecretException(InvalidUploadSecretException ex) {
		log.warn("Rejected catalog upload: {}", ex.getMessage());

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
		problemDetail.setTitle("Upload not allowed");

		return problemDetail;
	}
}
