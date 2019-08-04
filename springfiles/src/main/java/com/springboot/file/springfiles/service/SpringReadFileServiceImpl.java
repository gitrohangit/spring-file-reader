package com.springboot.file.springfiles.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.springboot.file.springfiles.model.User;
import com.springboot.file.springfiles.repository.SpringReadFileRepository;

@Service
@Transactional
public class SpringReadFileServiceImpl implements SpringReadFileService {

	@Autowired
	private SpringReadFileRepository springReadFileRepository;

	@Override
	public List<User> findAll() {
		return (List<User>) springReadFileRepository.findAll();
	}

	@Override
	public boolean saveDataFromUploadFile(MultipartFile file) {
		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("json")) {
			isFlag = readDataFromJSON(file);
		} else if (extension.equalsIgnoreCase("csv")) {
			isFlag = readDataFromCSV(file);
		} else if (extension.equalsIgnoreCase("xls")) {
			isFlag = readDataFromExcel(file);
		}
		return isFlag;
	}

	private boolean readDataFromExcel(MultipartFile file) {
		Workbook workbook = getWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		rows.next(); // to skip the heading
		while (rows.hasNext()) {
			Row row = rows.next();
			User user = new User();
			if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setFirstName(row.getCell(0).getStringCellValue());
			}
			if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setLastName(row.getCell(1).getStringCellValue());
			}
			if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setEmail(row.getCell(2).getStringCellValue());
			}
			if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) { // to confirm if phone number is coming as
																			// string or number
				String phoneNumber = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
				user.setPhoneNumber(phoneNumber);
			} else if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setEmail(row.getCell(2).getStringCellValue());
			}

			user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
			springReadFileRepository.save(user);
		}

		return true;
	}

	// to check the workbook extension
	private Workbook getWorkbook(MultipartFile file) {
		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		try {
			if (extension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			} else if (extension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(file.getInputStream());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return workbook;
	}

	private boolean readDataFromCSV(MultipartFile file) {
		try {
			InputStreamReader reader = new InputStreamReader(file.getInputStream());
			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			List<String[]> rows = csvReader.readAll();
			if (rows != null & rows.size() > 0) {
				for (String[] row : rows) {
					springReadFileRepository.save(new User(row[0], row[1], row[2], row[3],
							FilenameUtils.getExtension(file.getOriginalFilename())));
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean readDataFromJSON(MultipartFile file) {

		try {
			InputStream input = file.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			List<User> users = Arrays.asList(mapper.readValue(input, User[].class));
			if (users != null & users.size() > 0) {
				for (User user : users) {
					user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
					springReadFileRepository.save(user);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
