package com.example.koreatechfairy4.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.koreatechfairy4.domain.Lecture;
import com.example.koreatechfairy4.dto.GradeDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class LectureCrawler {
    public static GradeDto crawlLecture(Context context, Uri fileUri, String userId) {
        GradeDto result = null;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/User/" + userId + "/lectures");

        try {
            InputStream lectureListInputStream = context.getContentResolver().openInputStream(fileUri);
            HSSFWorkbook workbook = new HSSFWorkbook(lectureListInputStream);
            HSSFSheet lectureListSheet = workbook.getSheetAt(0); //0번째 시트만
            //0번째 시트만

            int firstRowIdx = lectureListSheet.getFirstRowNum();
            Log.d("first", String.valueOf(firstRowIdx));
            int lastRowIdx = lectureListSheet.getLastRowNum();
            Log.d("last", String.valueOf(lastRowIdx));


            int totalGrade = Integer.valueOf(lectureListSheet.getRow(lastRowIdx).getCell(9).getStringCellValue());
            double avgGrade = Double.valueOf(lectureListSheet.getRow(lastRowIdx).getCell(11).getStringCellValue());

            Set<String> lectureNames = new HashSet<>();

            double totalMajorGrade = 0.0; //평점 10.5
            int totalMajorCredit = 0; // 학점 3

            userRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("LectureCrawler", "New data has been added successfully.");
                } else {
                    Log.e("LectureCrawler", "Failed to delete existing data: " + task.getException());
                }
            });

            for (int i = firstRowIdx + 1; i <= lastRowIdx; ++i) {
                //for (int i=firstRowIdx+1; i<=10; ++i) {
                HSSFRow row = lectureListSheet.getRow(i);
                if (row != null) {
                    int lectureColumn = 5; // 교과목명
                    int domainColumn = 8; // 대표이수구분
                    int creditColumn = 9; // 학점
                    int gradeColumn = 11; //평점

                    String domainName = row.getCell(domainColumn).getStringCellValue();
                    String lectureName = row.getCell(lectureColumn).getStringCellValue();

                    if (!lectureName.equals("소 계") && !lectureName.equals("합 계")) {
                        userRef.child(lectureName).setValue(lectureName);
                    }

                    if(isValid(domainName) && !lectureNames.contains(lectureName)) {
                        lectureNames.add(lectureName);
                        totalMajorGrade += row.getCell(gradeColumn).getNumericCellValue();
                        totalMajorCredit += (int)row.getCell(creditColumn).getNumericCellValue();
                    }
                }
            }

            result = new GradeDto((totalMajorGrade / totalMajorCredit), avgGrade, totalGrade);
            Log.d("durudgus", String.valueOf(totalMajorGrade/totalMajorCredit));
            workbook.close();
            lectureListInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static boolean isValid(String domainName) {
        return domainName.equals("학부공통필수") || domainName.equals("학부공통선택");
    }
}
