package com.example.cokkiri.controller;

import com.example.cokkiri.model.TimeTable;
import com.example.cokkiri.model.User;
import com.example.cokkiri.service.TimeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class TimeTableController {
    @Autowired
    private TimeTableService timeTableService;

    @GetMapping ("/timeTable")
    public ResponseEntity<TimeTable> getLectureId(@RequestParam(value="id")String id){
        //학수 번호를 입력하면 과목에 대한 정보를 반환
        Optional<TimeTable> timeTable = timeTableService.findById(id);
        return new ResponseEntity<TimeTable>(timeTable.get(),HttpStatus.OK);
    }

    @GetMapping(value={"readTimeTable"})
    public ResponseEntity<String> readTimeTable(){
        return new ResponseEntity<String>(timeTableService.readTimeTable(), HttpStatus.OK);
    }
}
