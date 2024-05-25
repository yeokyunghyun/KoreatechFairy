package com.example.koreatechfairy4;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.koreatechfairy4.candidate.ResultCandi;
import com.example.koreatechfairy4.constants.TimeTab;
import com.example.koreatechfairy4.dto.GradeDto;
import com.example.koreatechfairy4.adapter.SearchLectureAdapter;
import com.example.koreatechfairy4.dto.LectureDto;
import com.example.koreatechfairy4.repository.LectureRepository;
import com.example.koreatechfairy4.util.DayAndTimes;
import com.example.koreatechfairy4.util.FilteringConditions;
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
    private final String year = "2024";
    private final String semester = "1";
    private ActivityResultLauncher<Intent> getContentLauncher;
    private Button lecture_register, my_page_button;
    private ImageButton schedule_back;
    private LectureRepository repository;
    private RecyclerView recyclerView;
    private RecyclerView myScheduleRecyclerView;
    private SearchLectureAdapter searchLectureAdapter;
    private LectureAdapter lectureAdapter;
    private List<LectureDto> lectureList;
    private List<LectureDto> myScheduleList = new ArrayList<>();
    private MyScheduleList myScheduleManager = MyScheduleList.getInstance();
    private TextView scheduleText;
    private String userMajor;
    private Spinner gradeSpinner, concentrationSpinner;
    private EditText et_major, et_general, et_MSC, et_HRD;
    private Button btn_create;
    private ImageButton btn_next;
    private TextView scheduleTextView;
    private Random random = new Random();
    private List<String> myLectures = new ArrayList<>();
    private MajorCandi majorCandi = new MajorCandi();
    private HRDCandi hrdCandi = new HRDCandi();
    private GeneralCandi generalCandi = new GeneralCandi();
    private MSCCandi mscCandi = new MSCCandi();
    private ResultCandi resultCandi = new ResultCandi();
    private int lectureIdx;


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

        // 검색 입력 처리
        EditText searchLecture = findViewById(R.id.search_lecture);
        searchLecture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchLectureAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String reference = "KoreatechFairy4/" + "schedule" + "/" + year + "/" + semester;

        String userId = getIntent().getStringExtra("userId");
        repository = new LectureRepository(reference);
        gradeSpinner = findViewById(R.id.sp_grade);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.grade,
                R.layout.spinner_item
        );
        concentrationSpinner = findViewById(R.id.sp_major);

        et_major = findViewById(R.id.et_major);
        et_general = findViewById(R.id.et_general);
        et_MSC = findViewById(R.id.et_MSC);
        et_HRD = findViewById(R.id.et_HRD);
        btn_create = (Button) findViewById(R.id.btn_create);
        btn_next = (ImageButton)findViewById(R.id.btn_next);

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

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference(reference);

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
                lectureIdx = 0;

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

                //추천 알고리즘 구현
                FilteringConditions filteringConditions = new FilteringConditions(grade, userMajor, concentration, majorCredit,
                        generalCredit, MSCCredit, HRDCredit);

                scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {//이 아래 전공들 있음
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        majorCandi.clear();
                        hrdCandi.clear();
                        generalCandi.clear();
                        mscCandi.clear();

                        for (DataSnapshot majorSnapshot : snapshot.getChildren()) {
                            List<List<LectureDto>> candiList;
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
                                                            if (data.getKey().equals("선택") && gradeSnapshot.getChildrenCount() == 1) {
                                                                candiList = new ArrayList<>();
                                                                addSelectiveCourses(candiList, new ArrayList<LectureDto>(), majorCredit, gradeSnapshot);
                                                                majorCandi.setMajorList(candiList);
                                                            }
                                                            else if (data.getKey().equals("필수")) {
                                                                //재귀 시작
                                                                candiList = new ArrayList<>();
                                                                int totalRequiredCredit = totalRequiredCredit(data);
                                                                for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                                    List<DataSnapshot> lectureNames = new ArrayList<>();
                                                                    for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                                        lectureNames.add(d);
                                                                    }
                                                                    createNonConcentration(candiList, lectureNames, majorCredit, majorCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                                }
                                                                majorCandi.setMajorList(candiList);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "에너지신소재화학공학부":
                                case "전기전자통신공학부":
                                case "메카트로닉스공학부":
                                    if (userMajor.equals(majorSnapshot.getKey())) {
                                        for (DataSnapshot concentrationSnapshot : majorSnapshot.getChildren()) {
                                            if (concentrationSnapshot.getKey().equals("전체")) {  //전체면 다른 과랑 동일
                                                for (DataSnapshot gradeSnapshot : concentrationSnapshot.getChildren()) {
                                                    if (gradeSnapshot.getKey().equals(grade)) {
                                                        for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                            if (data.getKey().equals("선택") && gradeSnapshot.getChildrenCount() == 1) {
                                                                candiList = new ArrayList<>();
                                                                addSelectiveCourses(candiList, new ArrayList<LectureDto>(), majorCredit, gradeSnapshot);
                                                                majorCandi.setMajorList(candiList);
                                                            }
                                                            else if (data.getKey().equals("필수")) {
                                                                //재귀 시작
                                                                candiList = new ArrayList<>();
                                                                int totalRequiredCredit = totalRequiredCredit(data);
                                                                List<DataSnapshot> lectureNames = new ArrayList<>();
                                                                for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                                    for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                                        lectureNames.add(d);
                                                                    }
                                                                }
                                                                createNonConcentration(candiList, lectureNames, majorCredit, majorCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                                majorCandi.setMajorList(candiList);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (concentrationSnapshot.getKey().equals(concentration)) {//전체가 아니면 세부전공 + 전체로 구성
                                                for (DataSnapshot gradeSnapshot : concentrationSnapshot.getChildren()) {    // 생시, 디시 등
                                                    DataSnapshot allGradeSnapshot = majorSnapshot.child("전체").child(grade);
                                                    if (gradeSnapshot.getKey().equals(grade)) { //같은 학년에 대해
                                                        for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                            if (data.getKey().equals("선택") && gradeSnapshot.getChildrenCount() == 1) { //전체 필수로 진입
                                                                candiList = new ArrayList<>();
                                                                int allTotalRequiredCredit = totalRequiredCredit(allGradeSnapshot.child("필수"));
                                                                for (DataSnapshot creditSnapshot : allGradeSnapshot.child("필수").getChildren()) {
                                                                    List<DataSnapshot> allLectureNames = new ArrayList<>();
                                                                    for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                                        allLectureNames.add(d);
                                                                    }
                                                                    createConcentrationWithRequired(candiList, allLectureNames, majorCredit, majorCredit, 0, new ArrayList<>(), gradeSnapshot, allGradeSnapshot, allTotalRequiredCredit);
                                                                }
                                                                majorCandi.setMajorList(candiList);
                                                            }
                                                            else if (data.getKey().equals("필수")) { //필수 과목에서 재귀 시작
                                                                //재귀 시작
                                                                candiList = new ArrayList<>();
                                                                int concenTotalRequiredCredit = totalRequiredCredit(data);
                                                                int allTotalRequiredCredit = totalRequiredCredit(allGradeSnapshot.child("필수"));    //세부전공 필수과목 학점 합 + 전체 필수과목 학점 합
                                                                for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                                    List<DataSnapshot> lectureNames = new ArrayList<>();
                                                                    for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                                        lectureNames.add(d);
                                                                    }
                                                                    createConcentration(candiList, lectureNames, majorCredit, majorCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot,
                                                                            allGradeSnapshot, concenTotalRequiredCredit, allTotalRequiredCredit);
                                                                }
                                                                majorCandi.setMajorList(candiList);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "교양": //2학년을 넘으면 0학년으로??
                                    String generalGrade = Integer.parseInt(grade) <= 2 ? grade : "0";
                                    for (DataSnapshot gradeSnapshot : majorSnapshot.getChildren()) {
                                        if (gradeSnapshot.getKey().equals(generalGrade)) {
                                            for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                if (data.getKey().equals("선택") && gradeSnapshot.getChildrenCount() == 1) {
                                                    candiList = new ArrayList<>();
                                                    addSelectiveCourses(candiList, new ArrayList<LectureDto>(), generalCredit, gradeSnapshot);
                                                    generalCandi.setGeneralList(candiList);
                                                }
                                                else if (data.getKey().equals("필수")) {
                                                    //재귀 시작
                                                    candiList = new ArrayList<>();
                                                    int totalRequiredCredit = totalRequiredCredit(data);
                                                    for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                        List<DataSnapshot> lectureNames = new ArrayList<>();
                                                        for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                            lectureNames.add(d);
                                                        }
                                                        createNonConcentration(candiList, lectureNames, generalCredit, generalCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                    }
                                                    generalCandi.setGeneralList(candiList);
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "HRD":
                                    for (DataSnapshot gradeSnapshot : majorSnapshot.getChildren()) {
                                        if (gradeSnapshot.getKey().equals(grade)) {
                                            for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                if (data.getKey().equals("선택") && gradeSnapshot.getChildrenCount() == 1) {
                                                    candiList = new ArrayList<>();
                                                    addSelectiveCourses(candiList, new ArrayList<LectureDto>(), HRDCredit, gradeSnapshot);
                                                    hrdCandi.setHRDList(candiList);
                                                }
                                                else if (data.getKey().equals("필수")) {
                                                    //재귀 시작
                                                    candiList = new ArrayList<>();
                                                    int totalRequiredCredit = totalRequiredCredit(data);
                                                    for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                        List<DataSnapshot> lectureNames = new ArrayList<>();
                                                        for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                            lectureNames.add(d);
                                                        }
                                                        createNonConcentration(candiList, lectureNames, HRDCredit, HRDCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                    }
                                                    hrdCandi.setHRDList(candiList);
                                                }
                                            }
                                        }
                                    }
                                    break;

                                case "MSC":
                                    for (DataSnapshot gradeSnapshot : majorSnapshot.getChildren()) {
                                        if (gradeSnapshot.getKey().equals(grade)) {
                                            for (DataSnapshot data : gradeSnapshot.getChildren()) {
                                                if (data.getKey().equals("선택") && gradeSnapshot.getChildrenCount() == 1) {
                                                    candiList = new ArrayList<>();
                                                    addSelectiveCourses(candiList, new ArrayList<LectureDto>(), MSCCredit, gradeSnapshot);
                                                    mscCandi.setMSCList(candiList);
                                                }
                                                else if (data.getKey().equals("필수")) {
                                                    //재귀 시작
                                                    candiList = new ArrayList<>();
                                                    int totalRequiredCredit = totalRequiredCredit(data);
                                                    for (DataSnapshot creditSnapshot : data.getChildren()) {
                                                        List<DataSnapshot> lectureNames = new ArrayList<>();
                                                        for (DataSnapshot d : creditSnapshot.getChildren()) {
                                                            lectureNames.add(d);
                                                        }
                                                        createNonConcentration(candiList, lectureNames, MSCCredit, MSCCredit, 0, new ArrayList<LectureDto>(), gradeSnapshot, totalRequiredCredit);
                                                    }
                                                    mscCandi.setMSCList(candiList);

                                                }
                                            }
                                        }
                                    }
                                    break;
                            }
                        }

                        // 결과 리스트 만들기

                        resultCandi.clear();
                        createResultCandi(new ArrayList<>(), 0, filteringConditions);
                        Collections.shuffle(resultCandi.getResultList(), new Random());

                        clearAllTables();
                        if (!resultCandi.isEmpty()) {
                            if (myScheduleManager.addAllLecture(resultCandi.getResultList().get(lectureIdx++))) {
                                for (LectureDto lectureDto : new ArrayList<>(myScheduleManager.getLectureList())) {
                                    createTable(lectureDto);
                                }
                            }
                        }
                        else {
                            Toast.makeText(ScheduleActivity.this, "This is a Toast message", Toast.LENGTH_SHORT).show();
                        }

                        btn_next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!resultCandi.isEmpty()) {
                                    clearAllTables();
                                    if (myScheduleManager.addAllLecture(resultCandi.getResultList().get(lectureIdx++))) {
                                        for (LectureDto lectureDto : new ArrayList<>(myScheduleManager.getLectureList())) {
                                            createTable(lectureDto);
                                        }
                                    }
                                    if (lectureIdx >= resultCandi.getResultList().size()) {
                                        lectureIdx = 0;
                                        Log.d("lecture", "초기화");
                                    }
                                }
                                else {
                                    Toast.makeText(ScheduleActivity.this, "This is a Toast message", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.d("close", "끝");

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

        lectureAdapter = new LectureAdapter(myScheduleList, lecture -> {
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
                    scheduleTextView = findViewById(resId);
                    scheduleTextView.setText(null);
                    scheduleTextView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }
            lectureAdapter.notifyDataSetChanged();
        });
        myScheduleRecyclerView.setAdapter(lectureAdapter);

        repository.getLectureDtoList(new LectureRepository.DataCallback() {
            @Override
            public void onCallback(List<LectureDto> lectureList) {
                searchLectureAdapter = new SearchLectureAdapter(lectureList, lecture -> {
                    //객체를 클릭했을 시에 반응
                    // 이름 겹치는 경우
                    if (myScheduleManager.isDuplicateName(lecture.getName())) {
                        // 중복 메시지 출력
                        showCustomToast("이미 시간표에 있는 과목입니다.");
                    }
                    else if(myLectures.contains(lecture.getName())) {
                        showCustomToast("이미 수강한 과목입니다.");
                    }
                    else {
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

                                    int textViewSize = dat.getTimeList().size();

                                    String lectureName = lecture.getName();
                                    String lectureClasses = lecture.getClasses();

                                    String abbreviationName = "";

                                    if (lectureName.charAt(0) >= 'A' && lectureName.charAt(0) <= 'Z') {
                                        abbreviationName += lectureName.substring(0, 3);
                                    } else {
                                        abbreviationName += lectureName.substring(0, 2);
                                    }

                                    abbreviationName += " " + lectureClasses;

                                    if (textViewSize == 1) {
                                        String t = dat.getTimeList().get(0);
                                        String scheduleId = scheduleDay + t;
                                        int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                        setTextWithId(resId, abbreviationName);
                                    } else if (textViewSize == 2) {
                                        String t = dat.getTimeList().get(0);
                                        String scheduleId = scheduleDay + t;
                                        int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                        setTextWithId(resId, lectureName);

                                        t = dat.getTimeList().get(1);
                                        scheduleId = scheduleDay + t;
                                        resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                        setTextWithId(resId, lectureClasses);
                                    } else { // textViewSize가 3 이상
                                        int i;
                                        for (i = 1; i <= textViewSize - 1; ++i) {
                                            int firstIdx = 4 * (i - 1);
                                            int lastIdx = 4 * i;
                                            // i가 마지막임
                                            if (i == textViewSize - 1) {
                                                String t = dat.getTimeList().get(i - 1);
                                                String scheduleId = scheduleDay + t;
                                                int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                                setTextWithId(resId, lectureName.substring(firstIdx, lectureName.length()));
                                                break;
                                            } else {
                                                if (lectureName.length() <= lastIdx) {
                                                    String t = dat.getTimeList().get(i - 1);
                                                    String scheduleId = scheduleDay + t;
                                                    int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                                    setTextWithId(resId, lectureName.substring(firstIdx, lectureName.length()));
                                                    break;
                                                } //
                                                else {
                                                    String t = dat.getTimeList().get(i - 1);
                                                    String scheduleId = scheduleDay + t;
                                                    int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                                    setTextWithId(resId, lectureName.substring(firstIdx, lastIdx));
                                                }
                                            }
                                        }
                                        // 분반 작성
                                        String t = dat.getTimeList().get(i);
                                        String scheduleId = scheduleDay + t;
                                        int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                                        setTextWithId(resId, lectureClasses);
                                    }


                                    for (String t : dat.getTimeList()) {
                                        myScheduleManager.addTime(day, t);
                                        String scheduleId = scheduleDay + t;
                                        int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());

                                        scheduleTextView = findViewById(resId);
                                        scheduleTextView.setBackgroundColor(randomColor);
                                    }
                                }
                                //2번 째 RecyclerView
                                myScheduleList.add(lecture);
                                lectureAdapter.notifyDataSetChanged();
                            }

                        } else { // 시간이 비어있음
                            // 시간이 없다 메시지 출력
                        }
                    }
                });
                recyclerView.setAdapter(searchLectureAdapter);
            }
        });
    }

    private void setTextWithId(int resId, String text) {
        scheduleTextView = findViewById(resId);
        scheduleTextView.setText(text);
        scheduleTextView.setTextSize(13.4f);
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
            for (int i = 0; i < TimeTab.timeTab.length; ++i) {
                if (firstTime.equals(TimeTab.timeTab[i][0])) {
                    isChecked = true;
                    for (int j = i; j < TimeTab.timeTab.length; ++j) {
                        timeList.add(TimeTab.timeTab[j][1]);
                        if (secondTime.equals(TimeTab.timeTab[j][0])) break;
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

    private void createNonConcentration(List<List<LectureDto>> list, List<DataSnapshot> lectureNames, int targetCredit, int remainingCredit, int idx, List<LectureDto> current, DataSnapshot gradeSnapshot, int totalRequiredCredit) {
        if (remainingCredit < 0) return;
        if (remainingCredit == 0) {
            if (!list.contains(current)) {
                list.add(new ArrayList<>(current));
            }
            return;
        }
        if (remainingCredit == targetCredit - totalRequiredCredit) {
            addSelectiveCourses(list, current, targetCredit - totalRequiredCredit, gradeSnapshot);
        }
        if (idx >= lectureNames.size()) return;

        List<DataSnapshot> classes = new ArrayList<>();
        for (DataSnapshot d : lectureNames.get(idx).getChildren()) {
            classes.add(d);
        }

        for (DataSnapshot lectureSnapshot : classes) {
            LectureDto lecture = lectureSnapshot.getValue(LectureDto.class);
            if (isPossible(current, lecture) && !myLectures.contains(lecture.getName())) {
                current.add(lecture);
                createNonConcentration(list, lectureNames, targetCredit, remainingCredit - lecture.getCredit(), idx + 1, current, gradeSnapshot, totalRequiredCredit);
                current.remove(current.size() - 1);
            }
        }
        createNonConcentration(list, lectureNames, targetCredit, remainingCredit, idx + 1, current, gradeSnapshot, totalRequiredCredit);

    }


    //세부전공
    private void createConcentration(List<List<LectureDto>> list, List<DataSnapshot> lectureNames, int majorCredit, int remainingCredit, int idx, List<LectureDto> current,
                                     DataSnapshot gradeSnapshot, DataSnapshot allGradeSnapshot, int concenTotalRequiredCredit, int allTotalRequiredCredit) {
        if (remainingCredit < 0) return;
        if (remainingCredit == 0) {
            if (!list.contains(current)) {
                list.add(new ArrayList<>(current));
            }
            return;
        }

        if (remainingCredit == majorCredit - concenTotalRequiredCredit) {

            List<DataSnapshot> allLectureNames = new ArrayList<>();
            for (DataSnapshot creditSnapshot : allGradeSnapshot.child("필수").getChildren()) {
                for (DataSnapshot d : creditSnapshot.getChildren()) {
                    allLectureNames.add(d);
                }
            }
            createConcentrationWithRequired(list, allLectureNames, remainingCredit, remainingCredit, 0, current, gradeSnapshot, allGradeSnapshot, allTotalRequiredCredit);
        }
        if (idx >= lectureNames.size()) return;

        List<DataSnapshot> classes = new ArrayList<>();
        for (DataSnapshot d : lectureNames.get(idx).getChildren()) {
            classes.add(d);
        }

        for (DataSnapshot lectureSnapshot : classes) {
            LectureDto lecture = lectureSnapshot.getValue(LectureDto.class);
            if (isPossible(current, lecture) && !myLectures.contains(lecture.getName())) {
                current.add(lecture);
                createConcentration(list, lectureNames, majorCredit, remainingCredit - lecture.getCredit(), idx + 1, current, gradeSnapshot,
                        allGradeSnapshot, concenTotalRequiredCredit, allTotalRequiredCredit);
                current.remove(current.size() - 1);
            }
        }
        createConcentration(list, lectureNames, majorCredit, remainingCredit, idx + 1, current, gradeSnapshot, allGradeSnapshot, concenTotalRequiredCredit, allTotalRequiredCredit);

    }

    //전체 필수
    private void createConcentrationWithRequired(List<List<LectureDto>> list, List<DataSnapshot> lectureNames, int majorCredit, int remainingCredit, int idx, List<LectureDto> current,
                                                 DataSnapshot gradeSnapshot, DataSnapshot allGradeSnapshot, int allTotalRequiredCredit) {
        if (remainingCredit < 0) return;
        if (remainingCredit == 0) {
            if (!list.contains(current)) {
                list.add(new ArrayList<>(current));
            }
            return;
        }
        if (remainingCredit == majorCredit - allTotalRequiredCredit) {
            //list.add(new ArrayList<>(current));
            addSelectiveCoursesWithConcentration(list, current, majorCredit - allTotalRequiredCredit, gradeSnapshot, allGradeSnapshot);
        }


        if (idx >= lectureNames.size()) return;

        List<DataSnapshot> classes = new ArrayList<>();
        for (DataSnapshot d : lectureNames.get(idx).getChildren()) {
            classes.add(d);
        }

        for (DataSnapshot lectureSnapshot : classes) {
            LectureDto lecture = lectureSnapshot.getValue(LectureDto.class);
            if (isPossible(current, lecture) && !myLectures.contains(lecture.getName())) {
                current.add(lecture);
                createNonConcentration(list, lectureNames, majorCredit, remainingCredit - lecture.getCredit(), idx + 1, current, gradeSnapshot, allTotalRequiredCredit);
                current.remove(current.size() - 1);
            }
        }
        createConcentrationWithRequired(list, lectureNames, majorCredit, remainingCredit, idx + 1, current, gradeSnapshot, allGradeSnapshot, allTotalRequiredCredit);

    }

    private void addSelectiveCoursesWithConcentration(List<List<LectureDto>> list, List<LectureDto> current, int remainingCredit, DataSnapshot gradeSnapshot, DataSnapshot allGradeSnapshot) {
        // 선택 과목 목록을 가져옵니다.
        DataSnapshot selectiveCoursesSnapshot = gradeSnapshot.child("선택");
        DataSnapshot selectiveCoursesSnapshot2 = allGradeSnapshot.child("선택");
        if (!selectiveCoursesSnapshot.exists() || !selectiveCoursesSnapshot2.exists()) return;

        List<DataSnapshot> selectiveCourses = new ArrayList<>();

        for (DataSnapshot d : selectiveCoursesSnapshot.getChildren()) {
            for (DataSnapshot ds : d.getChildren()) {
                selectiveCourses.add(ds);
            }
        }
        for (DataSnapshot d : selectiveCoursesSnapshot2.getChildren()) {
            for (DataSnapshot ds : d.getChildren()) {
                selectiveCourses.add(ds);
            }
        }

        addSelectiveCoursesWithConcentrationRecursive(list, current, remainingCredit, selectiveCourses, 0);
    }

    private void addSelectiveCoursesWithConcentrationRecursive(List<List<LectureDto>> list, List<LectureDto> current, int remainingCredit, List<DataSnapshot> selectiveCourses, int idx) {
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
                addSelectiveCoursesWithConcentrationRecursive(list, current, remainingCredit - lecture.getCredit(), selectiveCourses, idx + 1);
                current.remove(current.size() - 1);
            }
        }

        // 다음 선택 과목도 확인
        addSelectiveCoursesWithConcentrationRecursive(list, current, remainingCredit, selectiveCourses, idx + 1);
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

        for (LectureDto lectureDto : list) {
            if (lectureDto.getName().equals(lecture.getName())) return false;
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

    private void createResultCandi(List<LectureDto> current, int depth, FilteringConditions filteringConditions) {
        if (depth > 3) {
            resultCandi.add(new ArrayList<>(current));
            return;
        }
        switch (depth) {
            case 0:
                if (filteringConditions.getMajorCredit() != 0) {
                    for (List<LectureDto> majorList : majorCandi.getMajorList()) {  //전공은 다 넣어
                        current.addAll(majorList);
                        createResultCandi(current, depth + 1, filteringConditions);
                        current.removeAll(majorList);
                    }
                }
                else {
                    createResultCandi(current, depth + 1, filteringConditions);
                }
                break;
            case 1:
                if (filteringConditions.getGeneralCredit() != 0) {
                    for (List<LectureDto> generalList : generalCandi.getGeneralList()) {
                        if (isPossibleCandi(current, generalList)) {
                            current.addAll(generalList);
                            createResultCandi(current, depth + 1, filteringConditions);
                            current.removeAll(generalList);
                        }
                    }
                }
                else {
                    createResultCandi(current, depth + 1, filteringConditions);
                }
                break;
            case 2:
                if (filteringConditions.getHRDCredit() != 0) {
                    for (List<LectureDto> hrdList : hrdCandi.getHRDList()) {
                        if (isPossibleCandi(current, hrdList)) {
                            current.addAll(hrdList);
                            createResultCandi(current, depth + 1, filteringConditions);
                            current.removeAll(hrdList);
                        }
                    }
                }
                else {
                    createResultCandi(current, depth + 1, filteringConditions);
                }
                break;
            case 3:
                if (filteringConditions.getMSCCredit() != 0) {
                    for (List<LectureDto> mscList : mscCandi.getMSCList()) {
                        if (isPossibleCandi(current, mscList)) {
                            current.addAll(mscList);
                            createResultCandi(current, depth + 1, filteringConditions);
                            current.removeAll(mscList);
                        }
                    }
                }
                else {
                    createResultCandi(current, depth + 1, filteringConditions);
                }
                break;
        }
    }

    private boolean isPossibleCandi(List<LectureDto> list1, List<LectureDto> list2) {
        for (LectureDto lecture : list2) {
            if (!isPossible(list1, lecture)) {
                return false;
            }
        }
        return true;
    }

    private static List<String> splitStringByLength(String input, int length) {
        List<String> parts = new ArrayList<>();

        for (int i = 0; i < input.length(); i += length) {
            parts.add(input.substring(i, Math.min(i + length, input.length())));
        }

        return parts;
    }

    private void createTable(LectureDto lecture) {
        int minColorValue = 128;
        int red = random.nextInt(128) + minColorValue;
        int green = random.nextInt(128) + minColorValue;
        int blue = random.nextInt(128) + minColorValue;
        List<DayAndTimes> dayAndTimes = changeToDayAndTimes(lecture.getTime());
        // 다 되는 경우
        myScheduleManager.addLecture(lecture);
        for (DayAndTimes dat : dayAndTimes) {
            String day = dat.getDays();
            String scheduleDay = day + "_";
            int randomColor = Color.rgb(red, green, blue);

            int textViewSize = dat.getTimeList().size();

            String lectureName = lecture.getName();
            String lectureClasses = lecture.getClasses();

            String abbreviationName = "";

            if (lectureName.charAt(0) >= 'A' && lectureName.charAt(0) <= 'Z') {
                abbreviationName += lectureName.substring(0, 3);
            } else {
                abbreviationName += lectureName.substring(0, 2);
            }

            abbreviationName += " " + lectureClasses;

            if (textViewSize == 1) {
                String t = dat.getTimeList().get(0);
                String scheduleId = scheduleDay + t;
                int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                setTextWithId(resId, abbreviationName);
            } else if (textViewSize == 2) {
                String t = dat.getTimeList().get(0);
                String scheduleId = scheduleDay + t;
                int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                setTextWithId(resId, lectureName);

                t = dat.getTimeList().get(1);
                scheduleId = scheduleDay + t;
                resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                setTextWithId(resId, lectureClasses);
            } else { // textViewSize가 3 이상
                int i;
                for (i = 1; i <= textViewSize - 1; ++i) {
                    int firstIdx = 4 * (i - 1);
                    int lastIdx = 4 * i;
                    // i가 마지막임
                    if (i == textViewSize - 1) {
                        String t = dat.getTimeList().get(i - 1);
                        String scheduleId = scheduleDay + t;
                        int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                        setTextWithId(resId, lectureName.substring(firstIdx, lectureName.length()));
                        break;
                    } else {
                        if (lectureName.length() <= lastIdx) {
                            String t = dat.getTimeList().get(i - 1);
                            String scheduleId = scheduleDay + t;
                            int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                            setTextWithId(resId, lectureName.substring(firstIdx, lectureName.length()));
                            break;
                        } //
                        else {
                            String t = dat.getTimeList().get(i - 1);
                            String scheduleId = scheduleDay + t;
                            int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                            setTextWithId(resId, lectureName.substring(firstIdx, lastIdx));
                        }
                    }
                }
                // 분반 작성
                String t = dat.getTimeList().get(i);
                String scheduleId = scheduleDay + t;
                int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());
                setTextWithId(resId, lectureClasses);
            }
            for (String t : dat.getTimeList()) {
                myScheduleManager.addTime(day, t);
                String scheduleId = scheduleDay + t;
                int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());

                scheduleTextView = findViewById(resId);
                scheduleTextView.setBackgroundColor(randomColor);
            }
        }
        myScheduleList.add(lecture);
        lectureAdapter.notifyDataSetChanged();
    }

    private void clearAllTables() {
        for (LectureDto lecture : myScheduleList) {
            List<DayAndTimes> dayAndTimes = changeToDayAndTimes(lecture.getTime());

            for (DayAndTimes dat : dayAndTimes) {
                String day = dat.getDays();
                String scheduleDay = day + "_";

                for (String t : dat.getTimeList()) {
                    String scheduleId = scheduleDay + t;
                    int resId = getResources().getIdentifier(scheduleId, "id", getPackageName());

                    scheduleTextView = findViewById(resId);
                    scheduleTextView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white)); // 원래 배경색으로 설정
                    setTextWithId(resId, ""); // 텍스트 제거
                }
            }
        }

        // 모든 강의와 시간을 제거
        myScheduleManager.clearAll();
        myScheduleList.clear();
        lectureAdapter.notifyDataSetChanged();
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        // 이미지 및 텍스트 설정
        ImageView imageView = layout.findViewById(R.id.toast_image);
        imageView.setImageResource(R.drawable.smallfairy); // 원하는 이미지로 변경

        TextView textView = layout.findViewById(R.id.toast_text);
        textView.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}