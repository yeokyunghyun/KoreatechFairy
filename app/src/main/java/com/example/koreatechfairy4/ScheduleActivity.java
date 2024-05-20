package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.adapter.LectureAdapter;
import com.example.koreatechfairy4.dto.LectureDto;
import com.example.koreatechfairy4.repository.LectureRepository;
import com.example.koreatechfairy4.util.DayAndTimes;
import com.example.koreatechfairy4.util.MyScheduleList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private String[][] timeTab = {
            {"01A", "0900"},
            {"01B", "0930"},
            {"02A", "1000"},
            {"02B", "1030"},
            {"03A", "1100"},
            {"03B", "1130"},
            {"04A", "1200"},
            {"04B", "1230"},
            {"05A", "1300"},
            {"05B", "1330"},
            {"06A", "1400"},
            {"06B", "1430"},
            {"07A", "1500"},
            {"07B", "1530"},
            {"08A", "1600"},
            {"08B", "1630"},
            {"09A", "1700"},
            {"09B", "1730"},
    };
    private final String year = "2024";
    private final String semester = "1";
    private ActivityResultLauncher<Intent> getContentLauncher;
    private Button lecture_register, my_page_button;
    private ImageButton schedule_back;
    private LectureRepository repository;
    private RecyclerView recyclerView;
    private LectureAdapter lectureAdapter;
    private List<LectureDto> lectureList;
    private MyScheduleList myScheduleList = MyScheduleList.getInstance();
    private TextView scheduleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        lecture_register = findViewById(R.id.lecture_register);
//        lecture_register.setOnClickListener(v -> openDocument());


        //상단 툴바 시작
        schedule_back = findViewById(R.id.imgBtn_schedule_back);
        schedule_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_page_button = findViewById(R.id.btn_schedule_mypage);
        my_page_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
        //상단 툴바 끝

        String reference = "KoreatechFairy4/" + "schedule" + "/" + year + "/" + semester;

        String userId = getIntent().getStringExtra("userId");
        repository = new LectureRepository(reference);

/*        getContentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        // 이 URI를 사용하여 파일 내용을 읽습니다.
//                        GradeDto userGrade = LectureCrawler.crawlLecture(getApplicationContext(), uri);
                        repository.remove();
                        List<LectureDto> lectures = ScheduleCrawler.crawlLecture(getApplicationContext(), uri);
                        repository.save(lectures);
                    }
                });*/

        recyclerView = findViewById(R.id.schedule_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // DividerItemDecoration 추가
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        repository.getLectureDtoList(new LectureRepository.DataCallback() {
            @Override
            public void onCallback(List<LectureDto> lectureList) {
                lectureAdapter = new LectureAdapter(lectureList, lecture -> {
                    //객체를 클릭했을 시에 반응
                    if (!lecture.isClicked()) { //클릭이 안된 상태
                        // 이름 겹치는 경우
                        if (myScheduleList.isDuplicateName(lecture.getName())) {
                            // 중복 메시지 출력
                        }
                        else {
                            // time을 변환시키는 과정 필요
                            String time = lecture.getTime();
                            if (!time.isEmpty()) {
                                String days = String.valueOf(time.charAt(0)); // 첫 요일
                                String[] splitLectureTime = time.split(",");
                                List<DayAndTimes> dayAndTimes = new ArrayList<>();

                                for (String lectureTime : splitLectureTime) {
                                    if (!(lectureTime.charAt(0) >= '0' && lectureTime.charAt(0) <= '9')) {
                                        days = String.valueOf(lectureTime.charAt(0));
                                    }
                                    List<String> timeList = new ArrayList<>();

                                    // ~로 나눠서 시간 추출
                                    String[] splitTilde = lectureTime.split("~");
                                    int firstLength = splitTilde[0].length();
                                    String firstTime = splitTilde[0].substring(firstLength - 3, firstLength);
                                    String secondTime = splitTilde[1].substring(0, 3);
//                                Log.d("days", days);
//                                Log.d("firstTime", firstTime);
//                                Log.d("secondTime", secondTime);

                                    boolean isChecked = false;
                                    for (int i = 0; i < timeTab.length; ++i) {
                                        if (firstTime.equals(timeTab[i][0])) {
                                            isChecked = true;
                                            for (int j = i; j < timeTab.length; ++j) {
                                                timeList.add(timeTab[j][1]);
                                                if (secondTime.equals(timeTab[j][0])) break;
                                            }
                                        }
                                        if (isChecked) break;
                                    }
                                    if (!isChecked) {
                                        timeList.add("1800");
                                    }

                                    dayAndTimes.add(new DayAndTimes(days, timeList));
                                }

                                /*for (DayAndTimes d : dayAndTimes) {
                                    Log.d("Days", d.getDays());
                                    for (String t : d.getTimeList()) {
                                        Log.d("time", t);
                                    }
                                }*/
                                // dayAndTime내부에 것들 비교하면서 시간 중복 체크
                                if(myScheduleList.isDuplicateTime(dayAndTimes)) {
                                    // 시간 중복 메시지 출력
                                }
                                else {
                                    // 다 되는 경우
                                    myScheduleList.addLecture(lecture);
                                    for(DayAndTimes dat : dayAndTimes) {
                                        String day = dat.getDays();
                                        String scheduleDay = day + "_";
                                        for(String t : dat.getTimeList()) {
                                            myScheduleList.addTime(day, t);
                                            String scheduleId = scheduleDay + t;
                                            int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                            Log.d("packageName", getPackageName());
                                            scheduleText = findViewById(resId);

                                            scheduleText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                                        }
                                    }

                                    lecture.switchIsClicked();
                                    
                                    // 시간표 색칠
                                    
                                }

                            } else { // 시간이 비어있음
                                // 시간이 없다 메시지 출력
                            }
                        }
                    } else { //클릭이 돼서 들어가있음

//                        MyScheduleList.remove(lecture);
//                        resetTable(lecture);
                        lecture.switchIsClicked();
                    }
                });
                recyclerView.setAdapter(lectureAdapter);
            }
        });
    }

    private void openDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // XLSX 파일 타입
        getContentLauncher.launch(intent);
    }

}