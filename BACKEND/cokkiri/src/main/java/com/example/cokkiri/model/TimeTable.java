package com.example.cokkiri.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TimeTable {

    //학수번호
    @Id
    @Column
    private String id;

    //과목명
    @Column
    private String subjectName;

    //교원명
    @Column
    private String teacherName;

    //강의 날짜/시간
    @Column
    private String lectureDate;
}
