package com.example.koreatechfairy4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Random;

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
    private RecyclerView myScheduleRecyclerView;
    private LectureAdapter lectureAdapter;
    private LectureAdapter myScheduleAdapter;
    private List<LectureDto> lectureList;
    private List<LectureDto> myScheduleList = new ArrayList<>();
    private MyScheduleList myScheduleManager = MyScheduleList.getInstance();
    private TextView scheduleText;
    private Random random = new Random();

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

        // 두 번째 RecyclerView 설정
        myScheduleRecyclerView = findViewById(R.id.my_schedule_list);
        myScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myScheduleAdapter = new LectureAdapter(myScheduleList, lecture -> {
            // 여기다가 클릭했을 때 반응 MyScheduleList에 있는 거 없애고 myScheduleList에 있는 객체 없애고 recyclerView한테 말해주고
            String time = lecture.getTime();
            List<DayAndTimes> dayAndTimes = changeToDayAndTimes(time);
            myScheduleList.remove(lecture);
            myScheduleManager.removeLecture(lecture);
            myScheduleManager.removeTime(dayAndTimes);
            for (DayAndTimes dat : dayAndTimes) {
                String day = dat.getDays();
                String scheduleDay = day + "_";
                for (String t : dat.getTimeList()) {
                    String scheduleId = scheduleDay + t;
                    int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                    scheduleText = findViewById(resId);
                    scheduleText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }
            myScheduleAdapter.notifyDataSetChanged();
        });
        myScheduleRecyclerView.setAdapter(myScheduleAdapter);

        repository.getLectureDtoList(new LectureRepository.DataCallback() {
            @Override
            public void onCallback(List<LectureDto> lectureList) {
                lectureAdapter = new LectureAdapter(lectureList, lecture -> {
                    //객체를 클릭했을 시에 반응
                    // 이름 겹치는 경우
                    if (myScheduleManager.isDuplicateName(lecture.getName())) {
                        // 중복 메시지 출력
                    } else {
                        // time을 변환시키는 과정 필요
                        String time = lecture.getTime();
                        if (!time.isEmpty()) {
                            List<DayAndTimes> dayAndTimes = changeToDayAndTimes(time);

                            // dayAndTime내부에 것들 비교하면서 시간 중복 체크
                            if (myScheduleManager.isDuplicateTime(dayAndTimes)) {
                                // 시간 중복 메시지 출력
                            } else {
                                int minColorValue = 128;
                                int red = random.nextInt(128) + minColorValue;
                                int green = random.nextInt(128) + minColorValue;
                                int blue = random.nextInt(128) + minColorValue;
                                // 다 되는 경우
                                myScheduleManager.addLecture(lecture);
                                for (DayAndTimes dat : dayAndTimes) {
                                    String day = dat.getDays();
                                    String scheduleDay = day + "_";
                                    int randomColor = Color.rgb(red, green, blue);

                                    for (String t : dat.getTimeList()) {
                                        myScheduleManager.addTime(day, t);
                                        String scheduleId = scheduleDay + t;
                                        int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                        Log.d("packageName", getPackageName());
                                        scheduleText = findViewById(resId);
                                        scheduleText.setBackgroundColor(randomColor);                                    }
                                }
                                //2번 째 RecyclerView
                                myScheduleList.add(lecture);
                                myScheduleAdapter.notifyDataSetChanged();

                                // text 쓰기

                            }

                        } else { // 시간이 비어있음
                            // 시간이 없다 메시지 출력
                        }
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

    private List<DayAndTimes> changeToDayAndTimes(String time) {
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

        return dayAndTimes;
    }
}