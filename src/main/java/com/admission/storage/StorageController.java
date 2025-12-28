package com.admission.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;





@RestController
public class StorageController {
     
	@GetMapping("/uploads/{filename}")
	public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
		
		
		try {
			Path path = Paths.get(StorageService.DIRECTORY, filename);
			Resource resource = new UrlResource(path.toUri());
			
			
			if(!resource.isReadable()) {
				return ResponseEntity.notFound().build();
			}
			
			String contentType = Files.probeContentType(path);
			if(contentType == null) {
				contentType = "application/octet-stream";
				
			}
			
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.body(resource);
			
			
		}
		
		catch(Exception e) {
			return ResponseEntity.notFound().build();
		}
		
	}
	
}
