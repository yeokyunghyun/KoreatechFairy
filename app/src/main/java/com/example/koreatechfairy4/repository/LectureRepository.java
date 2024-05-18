package com.example.koreatechfairy4.repository;

import android.util.Log;

import com.example.koreatechfairy4.constants.MajorDomain;
import com.example.koreatechfairy4.dto.LectureDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LectureRepository {
    private FirebaseDatabase database;
    DatabaseReference userRef;
    private String reference;

    public LectureRepository(String reference) {
        this.reference = reference;
        this.database = FirebaseDatabase.getInstance();
        userRef = database.getReference(reference);
    }

    public void remove() {
        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ScheduleActivity", "New data has been added successfully.");
            } else {
                Log.e("ScheduleActivity", "Failed to delete existing data: " + task.getException());
            }
        });
    }

    public void save(List<LectureDto> lectures) {
        for (LectureDto lecture : lectures) {
            //lecture 1개씩 데이터 베이스에 저장


            //파이어베이스에 구분대로 나누기
            classifyLecture(lecture);
        }
    }

    private void classifyLecture(LectureDto lecture) {
        //userRef에는 2024/1까지 저장이 되어있고 여기다가 HRD/ 교양/ 전공으로 나눠서 분류하면 됨
        if (lecture.getDepartment().contains("HRD")) { //HRD - 필수/선택 - 학점 - 과목
            putLecture(lecture, "HRD");
        }
        else if (lecture.getDepartment().contains("교양")) {  //교양 - MSC - 학년 - 필수 - 학점 - 과목
            if (lecture.getDomain().contains("MSC")) {
                userRef.child("MSC").child(lecture.getGrade()).child("필수")
                        .child(String.valueOf(lecture.getCredit())).child(lecture.getName()).setValue(lecture);
            } else {  //      - 교양 - 필수/선택 - 학점 - 과목
                putLecture(lecture, "교양");
            }
        }
        else {
            //전공
            //해당 강의에 대해서 전공일 경우 어떤 과인지 -> 어떤 세부 전공인지(MSC 포함) -> 몇학년인지 -> 필수/선택 - 학점 - 과목
            for (MajorDomain domain : MajorDomain.values()) {
                String major = domain.major();

                switch (major) {
                    case "기계":
                    case "컴퓨터":
                    case "산업":
                    case "고용":
                        putNotConcentration(lecture, domain);
                        break;

                    case "메카": //메카전체, 생시 디시 제건 MSC
                        if (lecture.getDomain().contains("MSC")) {
                            putMSC(lecture, String.valueOf(domain));
                        }
                        else if (lecture.getRegisterDepartment().contains("메카")) {
                            putConcentration(lecture, domain, "메카");
                        }
                        else if (lecture.getRegisterDepartment().contains("생시")) {
                            putConcentration(lecture, domain, "생산시스템");
                        }
                        else if (lecture.getRegisterDepartment().contains("디시")) {
                            putConcentration(lecture, domain, "디지털시스템");
                        }
                        else {
                            putConcentration(lecture, domain, "제어시스템");
                        }
                        break;
                    case "전기": //전체=전통, 전기 전자 정통
                        if (lecture.getDomain().contains("MSC")) {
                            putMSC(lecture, String.valueOf(domain));
                        }
                        else if (lecture.getRegisterDepartment().contains("전통")) {
                            putConcentration(lecture, domain, "전전통");
                        }
                        else if (lecture.getRegisterDepartment().contains("전기")) {
                            putConcentration(lecture, domain, "전기");
                        }
                        else if (lecture.getRegisterDepartment().contains("전자")) {
                            putConcentration(lecture, domain, "전자");
                        }
                        else {
                            putConcentration(lecture, domain, "정보통신");
                        }
                        break;
                    case "디자인":
                        if (lecture.getDomain().contains("MSC")) {
                            putMSC(lecture, String.valueOf(domain));
                        }
                        else if (lecture.getRegisterDepartment().contains("디공")) {
                            putConcentration(lecture, domain, "디자인");
                        }
                        else {
                            putConcentration(lecture, domain, "건축");
                        }
                        break;
                    case "에너지": //전체 = 에화, 에신 화생=응공?
                        if (lecture.getDomain().contains("MSC")) {
                            putMSC(lecture, String.valueOf(domain));
                        }
                        else if (lecture.getRegisterDepartment().contains("에화")) {
                            putConcentration(lecture, domain, "에신화");
                        }
                        else if (lecture.getRegisterDepartment().contains("에신")) {
                            putConcentration(lecture, domain, "에너지신소재");
                        }
                        else if (lecture.getRegisterDepartment().contains("화생")) {
                            putConcentration(lecture, domain, "화학생명");
                        }
                        break;
                }

            }
        }
    }

    private void putNotConcentration(LectureDto lecture, MajorDomain domain) {
        if (lecture.getDomain().contains("MSC")) {
            putMSC(lecture, String.valueOf(domain));
        } else {  //학부 - 학년 - 필수/선택 - 학점 - 과목
            putLecture(lecture, String.valueOf(domain));
        }
    }

    private void putConcentration(LectureDto lecture, MajorDomain domain, String concentration) {
        if (lecture.getDomain().contains("필수")) {
            userRef.child(String.valueOf(domain)).child(concentration).child(lecture.getGrade())
                    .child("필수").child(String.valueOf(lecture.getCredit()))
                    .child(lecture.getName()).setValue(lecture);
        }
        else {
            userRef.child(String.valueOf(domain)).child(concentration).child(lecture.getGrade())
                    .child("선택").child(String.valueOf(lecture.getCredit()))
                    .child(lecture.getName()).setValue(lecture);
        }
    }


    private void putMSC(LectureDto lecture, String major) {
        userRef.child(major).child("MSC")
                .child(lecture.getGrade()).child("필수")
                .child(String.valueOf(lecture.getCredit())).child(lecture.getName()).setValue(lecture);
    }

    private void putLecture(LectureDto lecture, String major) {
        if (lecture.getDomain().contains("필수")) {
            userRef.child(major).child(lecture.getGrade())
                    .child("필수").child(String.valueOf(lecture.getCredit()))
                    .child(lecture.getName()).setValue(lecture);
        } else {
            userRef.child(major).child(lecture.getGrade())
                    .child("선택").child(String.valueOf(lecture.getCredit()))
                    .child(lecture.getName()).setValue(lecture);
        }
    }
}
