package com.example.cokkiri.service;

import com.example.cokkiri.model.TimeTable;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.TimeTableRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TimeTableService {
    @Autowired
    private TimeTableRepository timeTableRepository;

    public Optional<TimeTable> findById(String id){
        Optional<TimeTable> timeTable = timeTableRepository.findById(id);
        return timeTable;
    }

    public String readTimeTable() {
        // 엑셀 파일을 읽어들입니다.
        FileInputStream fileInputStream;

        {
            try {
                fileInputStream = new FileInputStream(new File("timeTable.xlsx"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Workbook workbook;

        {
            try {
                workbook = new XSSFWorkbook(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 데이터를 변환합니다.
        List<TimeTable> dataList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            TimeTable data = new TimeTable(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue(), row.getCell(3).getStringCellValue());
            dataList.add(data);
        }

        //db에 저장
        for (TimeTable data : dataList) {
            timeTableRepository.save(data);
        }

        return "TimeTable data imported successfully.";
    }
}

