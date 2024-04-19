package com.example.koreatechfairy4.util;

import android.content.Context;
import android.net.Uri;

import com.example.koreatechfairy4.domain.Lecture;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;

import lombok.Getter;

@Getter
public class LectureCrawler {
    public static ArrayList<Lecture> crawlLecture(Context context, Uri fileUri) {
        ArrayList<Lecture> lectures = new ArrayList<>();

        try {
            InputStream lectureListInputStream = context.getContentResolver().openInputStream(fileUri);
            HSSFWorkbook workbook = new HSSFWorkbook(lectureListInputStream);
            HSSFSheet lectureListSheet = workbook.getSheetAt(0); //0번째 시트만
            //0번째 시트만

            int firstRowIdx = lectureListSheet.getFirstRowNum();
            int lastRowIdx = lectureListSheet.getLastRowNum();

            for (int i = lastRowIdx; i >= firstRowIdx + 1; --i) {
                //for (int i=firstRowIdx+1; i<=10; ++i) {
                HSSFRow row = lectureListSheet.getRow(i);
                if (row != null) {
                    int lectureNameColumn = 5;
                    int profColumn = 7; // 담당교수
                    int domainColumn = 8; // 대표이수구분
                    int creditColumn = 9; // 학점

                    String lectureName = row.getCell(lectureNameColumn).getStringCellValue();
                    String prof = row.getCell(profColumn).getStringCellValue();
                    if (prof.isEmpty()) continue;
                    String domainName = row.getCell(domainColumn).getStringCellValue();
                    int credit = (int) row.getCell(creditColumn).getNumericCellValue();

                    Lecture lecture = new Lecture.Builder().lectureName(lectureName).domain(domainName).credit(credit).prof(prof).build();

                    lectures.add(lecture);
                }
            }
            workbook.close();
            lectureListInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lectures;
    }
}
