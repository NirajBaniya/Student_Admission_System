package com.admission.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

	public static String DIRECTORY = "uploads";
	
	// Maximum file size: 5MB
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
	
	// Allowed image file extensions
	private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
	
	// Allowed document file extensions (for applications)
	private static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {".pdf", ".jpg", ".jpeg", ".png"};

	/**
	 * Stores the given MultipartFile in a specific upload directory 
	 * @param file
	 * @return Filename without path
	 * @throws IOException
	 * @throws IllegalArgumentException if file size exceeds limit or file type is not allowed
	 */
	public String store(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		// DONE: max file size limit
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
		}

		// extract file extension from original filename
		String originalFileName = file.getOriginalFilename();
		if (originalFileName == null) {
			throw new IllegalArgumentException("File name cannot be null");
		}
		
		String lowerFileName = originalFileName.toLowerCase();
		int lastIndex = lowerFileName.lastIndexOf(".");
		String fileExtension = "";
		if (lastIndex > -1) {
			fileExtension = lowerFileName.substring(lastIndex);
		}
		
		// DONE: Validate file type
		boolean isAllowed = false;
		for (String allowedExt : ALLOWED_IMAGE_EXTENSIONS) {
			if (fileExtension.equals(allowedExt)) {
				isAllowed = true;
				break;
			}
		}
		
		if (!isAllowed) {
			throw new IllegalArgumentException("File type not allowed. Only image files (JPG, JPEG, PNG, GIF, WEBP) are permitted");
		}
		
		// generate an unique filename for saving
		String fileName = Instant.now().toEpochMilli() + fileExtension;

		Path path = Paths.get(DIRECTORY, fileName);

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		return fileName;

	}
	
	/**
	 * Stores document files (PDF, JPG, etc.) for applications
	 * @param file
	 * @return Filename without path
	 * @throws IOException
	 * @throws IllegalArgumentException if file size exceeds limit or file type is not allowed
	 */
	public String storeDocument(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		// Max file size: 10MB for documents
		long maxDocumentSize = 10 * 1024 * 1024; // 10MB
		if (file.getSize() > maxDocumentSize) {
			throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
		}

		String originalFileName = file.getOriginalFilename();
		if (originalFileName == null) {
			throw new IllegalArgumentException("File name cannot be null");
		}
		
		String lowerFileName = originalFileName.toLowerCase();
		int lastIndex = lowerFileName.lastIndexOf(".");
		String fileExtension = "";
		if (lastIndex > -1) {
			fileExtension = lowerFileName.substring(lastIndex);
		}
		
		// Validate document file type
		boolean isAllowed = false;
		for (String allowedExt : ALLOWED_DOCUMENT_EXTENSIONS) {
			if (fileExtension.equals(allowedExt)) {
				isAllowed = true;
				break;
			}
		}
		
		if (!isAllowed) {
			throw new IllegalArgumentException("File type not allowed. Only PDF, JPG, JPEG, and PNG files are permitted");
		}
		
		// Generate unique filename
		String fileName = Instant.now().toEpochMilli() + fileExtension;

		Path path = Paths.get(DIRECTORY, fileName);

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	}

}