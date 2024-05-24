package com.example.koreatechfairy4;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreatechfairy4.adapter.LectureAdapter;
import com.example.koreatechfairy4.candidate.GeneralCandi;
import com.example.koreatechfairy4.candidate.HRDCandi;
import com.example.koreatechfairy4.candidate.MSCCandi;
import com.example.koreatechfairy4.candidate.MajorCandi;
import com.example.koreatechfairy4.dto.GradeDto;
import com.example.koreatechfairy4.dto.LectureDto;
import com.example.koreatechfairy4.repository.LectureRepository;
import com.example.koreatechfairy4.util.FilteringConditions;
import com.example.koreatechfairy4.util.DayAndTimes;
import com.example.koreatechfairy4.util.LectureCrawler;
import com.example.koreatechfairy4.util.MyScheduleList;
import com.example.koreatechfairy4.util.ScheduleCrawler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
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
    private String userMajor;
    private Spinner gradeSpinner, concentrationSpinner;
    private EditText et_major, et_general, et_MSC, et_HRD;
    private Button btn_create;
    private Random random = new Random();
    private List<String> myLectures = new ArrayList<>();
    private List<LectureDto> majorList, HRDList, MSCList, generalList;
    private MajorCandi majorCandi = new MajorCandi();
    private HRDCandi hrdCandi = new HRDCandi();
    private GeneralCandi generalCandi = new GeneralCandi();
    private MSCCandi mscCandi = new MSCCandi();


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

        lecture_register = findViewById(R.id.lecture_register);
        lecture_register.setOnClickListener(v -> openDocument());


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

        gradeSpinner = findViewById(R.id.sp_grade);
        concentrationSpinner = findViewById(R.id.sp_major);
        et_major = findViewById(R.id.et_major);
        et_general = findViewById(R.id.et_general);
        et_MSC = findViewById(R.id.et_MSC);
        et_HRD = findViewById(R.id.et_HRD);
        btn_create = (Button) findViewById(R.id.btn_create);

        //전공에 따라 Spinner 설정
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("KoreatechFairy4/User/" + userId);
        userRef.child("major").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 데이터가 존재하면, 전공 설정
                    userMajor = dataSnapshot.getValue(String.class);
                    if (userMajor != null) {
                        setSpinnerValues(userMajor);
                    } else {
                        Log.e("ScheduleActivity", "User major is null");
                        // 기본값 설정 또는 오류 처리
                        setSpinnerValues("default_major");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });

        //내 강의 리스트 가져오기
        userRef.child("lectures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myLectures.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        myLectures.add(dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HRDList = new ArrayList<>();
        MSCList = new ArrayList<>();
        generalList = new ArrayList<>();

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference(reference);


        List<LectureDto> testList = new ArrayList<>();
        //시간표 추천 버튼
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grade = gradeSpinner.getSelectedItem().toString();
                String concentration = concentrationSpinner.getSelectedItem().toString();
                int majorCredit = Integer.parseInt(converter(et_major.getText().toString()));
                int generalCredit = Integer.parseInt(converter(et_general.getText().toString()));
                int MSCCredit = Integer.parseInt(converter(et_MSC.getText().toString()));
                int HRDCredit = Integer.parseInt(converter(et_HRD.getText().toString()));

                userRef.child("major").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 데이터가 존재하면, 전공 설정
                            userMajor = dataSnapshot.getValue(String.class);
                            if (userMajor == null) {
                                Log.e("ScheduleActivity", "User major is null");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 데이터를 가져오는 도중 에러가 발생한 경우, 에러 처리를 하세요
                        Log.w("TAG", "Failed to read value.", databaseError.toException());
                    }
                });

                FilteringConditions filteringConditions
                        = new FilteringConditions(grade, userMajor, concentration, majorCredit, generalCredit,
                        MSCCredit, HRDCredit);

                //추천 알고리즘 구현

                scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {//이 아래 전공들 있음
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                            List<List<LectureDto>> testList;
                            switch (majorSnapshot.getKey()) {
                                case "컴퓨터공학부":
                                case "산업경영학부":
                                case "건축공학부":
                                case "고용서비스정책학과":
                                case "기계공학부":
                                case "디자인공학부":
                                    if (userMajor.equals(majorSnapshot.getKey())) {
                                        for (DataSnapshot concentrationSnapshot : majorSnapshot.getChildren()) {
                                            if (concentrationSnapshot.getKey().equals(concentration)) {
                                                for (DataSnapshot gradeSnapshot : concentrationSnapshot.getChildren()) {
                                                    if (gradeSnapshot.getKey().equals(grade)) {
                                                        for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                            if (data.getKey().equals("필수")) {
                                                                //재귀 시작
                                                                testList = new ArrayList<>();
                                                                int totalRequiredCredit = totalRequiredCredit(data);
                                                                for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                                    List<DataSnapshot> lectureNames = new ArrayList<>();
                                                                    for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                                        lectureNames.add(d);
                                                                    }
                                                                    test(testList, lectureNames, majorCredit, majorCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                                }
                                                                majorCandi.setMajorList(testList);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "메카트로닉스공학부":

                                    break;

                                case "에너지신소재화학공학부":

                                    break;

                                case "전기전자통신공학부":

                                    break;

                                case "교양": //2학년을 넘으면 0학년으로??
                                    String generalGrade = Integer.parseInt(grade) <= 2 ? grade : "0";
                                    for (DataSnapshot gradeSnapshot : majorSnapshot.getChildren()) {
                                        if (gradeSnapshot.getKey().equals(generalGrade)) {
                                            for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                if (data.getKey().equals("필수")) {
                                                    //재귀 시작
                                                    testList = new ArrayList<>();
                                                    int totalRequiredCredit = totalRequiredCredit(data);
                                                    for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                        List<DataSnapshot> lectureNames = new ArrayList<>();
                                                        for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                            lectureNames.add(d);
                                                        }
                                                        test(testList, lectureNames, generalCredit, generalCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                    }
                                                    generalCandi.setGeneralList(testList);
                                                    Log.d("count", String.valueOf(generalCandi.getGeneralList().size()));
                                                    Log.d("lecture", generalCandi.getGeneralList().toString());

                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "HRD":
                                    for (DataSnapshot gradeSnapshot : majorSnapshot.getChildren()) {
                                        if (gradeSnapshot.getKey().equals(grade)) {
                                            for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                if (data.getKey().equals("필수")) {
                                                    //재귀 시작
                                                    testList = new ArrayList<>();
                                                    int totalRequiredCredit = totalRequiredCredit(data);
                                                    for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                        List<DataSnapshot> lectureNames = new ArrayList<>();
                                                        for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                            lectureNames.add(d);
                                                        }
                                                        test(testList, lectureNames, HRDCredit, HRDCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "MSC":
                                    for (DataSnapshot gradeSnapshot : majorSnapshot.getChildren()) {
                                        if (gradeSnapshot.getKey().equals(grade)) {
                                            for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                if (data.getKey().equals("필수")) {
                                                    //재귀 시작
                                                    testList = new ArrayList<>();
                                                    int totalRequiredCredit = totalRequiredCredit(data);
                                                    for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                        List<DataSnapshot> lectureNames = new ArrayList<>();
                                                        for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                            lectureNames.add(d);
                                                        }
                                                        test(testList, lectureNames, MSCCredit, MSCCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                    }
                                                    mscCandi.setMSCList(testList);
                                                    Log.d("count", String.valueOf(mscCandi.getMSCList().size()));
                                                    Log.d("lecture", mscCandi.getMSCList().toString());

                                                }
                                            }
                                        }
                                    }
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        //강의목록 추가 버튼
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        // 이 URI를 사용하여 파일 내용을 읽습니다.
                        GradeDto userGrade = LectureCrawler.crawlLecture(getApplicationContext(), uri, userId);
                        repository.remove();
                        List<LectureDto> lectures = ScheduleCrawler.crawlLecture(getApplicationContext(), uri);
                        repository.save(lectures);
                    }
                });

        //시간표 알고리즘 부분 ---------------------------------------------------------

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
                            } else { //색칠 부분
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
                                        scheduleText.setBackgroundColor(randomColor);
                                    }
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

    private void setSpinnerValues(String major) {
        int arrayId = getResources().getIdentifier(major, "array", getPackageName());

        if (arrayId == 0) {
            arrayId = R.array.예외;
        }

        // 배열 리소스를 사용하여 어댑터를 생성합니다.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Spinner에 어댑터를 설정합니다.
        concentrationSpinner.setAdapter(adapter);
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

    private String converter(String str) {
        if (str.equals("")) return "0";
        return str;
    }

    private int currentCredit(List<LectureDto> list) {
        int currCredit = 0;
        for (LectureDto lectureDto : list) {
            currCredit += lectureDto.getCredit();
        }

        return currCredit;
    }

    private void test(List<List<LectureDto>> list, List<DataSnapshot> lectureNames, int majorCredit, int remainingCredit, int idx, List<LectureDto> current, DataSnapshot gradeSnapshot, int totalRequiredCredit) {
        if (remainingCredit < 0) return;
        if (remainingCredit == 0) {
            if (!list.contains(current)) {
                list.add(new ArrayList<>(current));
            }
            return;
        }
        if (remainingCredit == majorCredit - totalRequiredCredit) {
            //list.add(new ArrayList<>(current));
            addSelectiveCourses(list, current, majorCredit - totalRequiredCredit, gradeSnapshot);
        }
        if (idx >= lectureNames.size()) return;

//        String name = lectureNames.get(idx).getValue(String.class);
        List<DataSnapshot> classes = new ArrayList<>();
        for (DataSnapshot d : lectureNames.get(idx).getChildren()) {
            classes.add(d);
        }

        for (DataSnapshot lectureSnapshot : classes) {
            LectureDto lecture = lectureSnapshot.getValue(LectureDto.class);
            if (isPossible(current, lecture) && !myLectures.contains(lecture.getName())) {
                current.add(lecture);
                test(list, lectureNames, majorCredit, remainingCredit - lecture.getCredit(), idx + 1, current, gradeSnapshot, totalRequiredCredit);
                current.remove(current.size() - 1);
            }
        }
        test(list, lectureNames, majorCredit, remainingCredit, idx + 1, current, gradeSnapshot, totalRequiredCredit);

    }

    private void addSelectiveCourses(List<List<LectureDto>> list, List<LectureDto> current, int remainingCredit, DataSnapshot gradeSnapshot) {
        // 선택 과목 목록을 가져옵니다.
        DataSnapshot selectiveCoursesSnapshot = gradeSnapshot.child("선택");
        if (!selectiveCoursesSnapshot.exists()) return;

        List<DataSnapshot> selectiveCourses = new ArrayList<>();
        for (DataSnapshot d : selectiveCoursesSnapshot.getChildren()) {
            for (DataSnapshot ds : d.getChildren()) {
                selectiveCourses.add(ds);
            }
        }

        addSelectiveCoursesRecursive(list, current, remainingCredit, selectiveCourses, 0);
    }

    private void addSelectiveCoursesRecursive(List<List<LectureDto>> list, List<LectureDto> current, int remainingCredit, List<DataSnapshot> selectiveCourses, int idx) {
        if (remainingCredit < 0) return;
        if (remainingCredit == 0) {
            if (!list.contains(current)) {
                list.add(new ArrayList<>(current));
            }
            return;
        }
        if (idx >= selectiveCourses.size()) return;

        DataSnapshot selectiveCourseSnapshot = selectiveCourses.get(idx);
        for (DataSnapshot lectureSnapshot : selectiveCourseSnapshot.getChildren()) {
            LectureDto lecture = lectureSnapshot.getValue(LectureDto.class);
            if (isPossible(current, lecture) && !myLectures.contains(lecture.getName())) {
                current.add(lecture);
                addSelectiveCoursesRecursive(list, current, remainingCredit - lecture.getCredit(), selectiveCourses, idx + 1);
                current.remove(current.size() - 1);
            }
        }

        // 다음 선택 과목도 확인
        addSelectiveCoursesRecursive(list, current, remainingCredit, selectiveCourses, idx + 1);
    }

    private boolean isPossible(List<LectureDto> list, LectureDto lecture) {

        if (lecture.getTime() == null || lecture.getTime().isEmpty()) {
            return false;
        }

        List<DayAndTimes> currTimes = changeToDayAndTimes(lecture.getTime());
        for (LectureDto lectureDto : list) {
            if (lectureDto.getTime() == null || lectureDto.getTime().isEmpty()) {
                continue;
            }
            List<DayAndTimes> lectureTimes = changeToDayAndTimes(lectureDto.getTime());
            for (DayAndTimes currTime : currTimes) {
                for (DayAndTimes lectureTime : lectureTimes) {
                    if (currTime.getDays().equals(lectureTime.getDays()) && !Collections.disjoint(currTime.getTimeList(), lectureTime.getTimeList())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int totalRequiredCredit(DataSnapshot snapshot) {
        int total = 0;
        for (DataSnapshot d : snapshot.getChildren()) {
            int count = 0;
            for (DataSnapshot ds : d.getChildren()) {
                if (!myLectures.contains(ds.getKey())) {
                    ++count;
                }
            }
            total += Integer.parseInt(d.getKey()) * count;
        }
        return total;
    }
}