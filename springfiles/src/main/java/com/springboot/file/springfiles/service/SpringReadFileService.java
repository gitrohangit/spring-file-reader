package com.springboot.file.springfiles.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.springboot.file.springfiles.model.User;

public interface SpringReadFileService {

	List<User> findAll();

	boolean saveDataFromUploadFile(MultipartFile file);

}
