package com.example.koreatechfairy4.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.koreatechfairy4.dto.LectureDto;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ScheduleCrawler {
    public static List<LectureDto> crawlLecture(Context context, Uri fileUri) {

        List<LectureDto> lectureList = new ArrayList<>();

        try {
            InputStream lectureListInputStream = context.getContentResolver().openInputStream(fileUri);
            Workbook workbook = new XSSFWorkbook(lectureListInputStream);
            Sheet lectureListSheet = workbook.getSheetAt(0); //0번째 시트만
            //0번째 시트만

            int firstRowIdx = lectureListSheet.getFirstRowNum();
            Log.d("first", String.valueOf(firstRowIdx));
            int lastRowIdx = lectureListSheet.getLastRowNum();
            Log.d("last", String.valueOf(lastRowIdx));

            for (int i = firstRowIdx + 1; i <= lastRowIdx; ++i) {
                Row row = lectureListSheet.getRow(i);
                if (row != null) {
                    int codeColumn = 3; //과목코드 줄
                    int nameColumn = 4; //교과목명
                    int classesColumn = 5; //분반 class가 안됨
                    int domainColumn = 7; //대표이수구분
                    int creditColumn = 8; //학점 // 통화로 되어있음
                    int departmentColumn = 12; //개설학부
                    int gradeColumn = 14; //학년
                    int professorColumn = 15; // 교수
                    int timeColumn = 17; // 강의가능시간
                    int registerDepartmentColumn = 18; // 가능 학번


                    String code = row.getCell(codeColumn).getStringCellValue();
                    String name = row.getCell(nameColumn).getStringCellValue();
                    String classes = row.getCell(classesColumn).getStringCellValue();
                    String domain = row.getCell(domainColumn).getStringCellValue();
                    int credit = (int)row.getCell(creditColumn).getNumericCellValue();
                    String department = row.getCell(departmentColumn).getStringCellValue();

                    String grade = null;

                    if(row.getCell(gradeColumn).getCellType() == CellType.NUMERIC) {
                        grade = String.valueOf((int)row.getCell(gradeColumn).getNumericCellValue());
                    }
                    else {
                        grade = row.getCell(gradeColumn).getStringCellValue();
                    }

                    String professor = row.getCell(professorColumn).getStringCellValue();
                    String time = row.getCell(timeColumn).getStringCellValue();
                    String registerDepartment = row.getCell(registerDepartmentColumn).getStringCellValue();

                    LectureDto lecture = new LectureDto(code, name, classes, domain, credit, department, grade, professor, time, registerDepartment);
                    lectureList.add(lecture);
                    Log.d(Integer.toString(i), lecture.toString());
                }
            }

            workbook.close();
            lectureListInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lectureList;
    }
}
