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

	/**
	 * Stores the given MultipartFile in a specific upload directory 
	 * @param file
	 * @return Filename without path
	 * @throws IOException
	 */
	public String store(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		// TODO: max file size limit

		// extract file extension from original filename
		String originalFileName = file.getOriginalFilename().toLowerCase();
		int lastIndex = originalFileName.lastIndexOf(".");
		String fileExtension = "";
		if (lastIndex > -1) {
			fileExtension = originalFileName.substring(lastIndex);
		}
		// generate an unique filename for saving
		String fileName = Instant.now().toEpochMilli() + fileExtension;

		Path path = Paths.get(DIRECTORY, fileName);

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		return fileName;

	}

}