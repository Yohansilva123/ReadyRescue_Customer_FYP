package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class QuizQuestionsActivity extends AppCompatActivity {
    public TextView mQuestions, mScore, mFinalScore;
    private RadioButton mRadioBtn, mRadioBtn1, mRadioBtn2, mRadioBtn3, mRadioBtn4;
    private RadioGroup mAnswerGroup;
    private Button mNext, mDone, mViewPoints;
    private Dialog completeDialog;
    private int score = 0;
    private int i = 0;
    public DataSnapshot points;
    private String userId;
    private int totalScore;

    public ArrayList<String> questionsArrayList = new ArrayList<>();
    public ArrayList<String> answersArrayList1 = new ArrayList<>();
    public ArrayList<String> answersArrayList2 = new ArrayList<>();
    public ArrayList<String> answersArrayList3 = new ArrayList<>();
    public ArrayList<String> answersArrayList4 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);

        getDBSnapshot();

        setNavigation();

        setUpQuestion();

        setUpAnswers();

        setUpQuiz();
    }

    private void setUpQuestion() {
        questionsArrayList.add(" What is the best time to service a vehicle");
        questionsArrayList.add(" What is the best time to service a vehicle2");
        questionsArrayList.add(" What is the best time to service a vehicle3");
        questionsArrayList.add(" What is the best time to service a vehicle4");
    }

    private void setUpAnswers() {
        answersArrayList1.add("L1 Answer 1");
        answersArrayList2.add("L1 Right Answer 2");
        answersArrayList3.add("L1 Answer 3");
        answersArrayList4.add("L1 Answer 4");

        answersArrayList1.add("L2 Answer 1");
        answersArrayList2.add("L2 Answer 2");
        answersArrayList3.add("L2 Right Answer 3");
        answersArrayList4.add("L2 Answer 4");

        answersArrayList1.add("L3 Right Answer 1");
        answersArrayList2.add("L3 Answer 2");
        answersArrayList3.add("L3 Answer 3");
        answersArrayList4.add("L3 Answer 4");

        answersArrayList1.add("L4 Answer 1");
        answersArrayList2.add("L4 Answer 2");
        answersArrayList3.add("L4 Answer 3");
        answersArrayList4.add("L4 Right Answer 4");
    }

    private void setUpQuiz() {
        mQuestions = findViewById(R.id.question);
        mAnswerGroup = findViewById(R.id.answerGroup);
        mRadioBtn1 = findViewById(R.id.answer1);
        mRadioBtn2 = findViewById(R.id.answer2);
        mRadioBtn3 = findViewById(R.id.answer3);
        mRadioBtn4 = findViewById(R.id.answer4);
        mNext = findViewById(R.id.next_question);
        mScore = findViewById(R.id.score);
        completeDialog = new Dialog(this);

            mQuestions.setText(questionsArrayList.get(i));
            mRadioBtn1.setText(answersArrayList1.get(i));
            mRadioBtn2.setText(answersArrayList2.get(i));
            mRadioBtn3.setText(answersArrayList3.get(i));
            mRadioBtn4.setText(answersArrayList4.get(i));

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int selectedID = mAnswerGroup.getCheckedRadioButtonId();
                    mRadioBtn = findViewById(selectedID);
                    if (mRadioBtn!=null){
                        switch (i){
                            case 0:
                                if (mRadioBtn.getText().toString().contains(answersArrayList2.get(0))){
                                    score = score + 5;
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was correct", Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was wrong", Toast.LENGTH_LONG).show();
                                break;

                            case 1:
                                if (mRadioBtn.getText().toString().contains(answersArrayList3.get(1))){
                                    score = score + 5;
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was correct", Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was wrong", Toast.LENGTH_LONG).show();
                                break;

                            case 2:
                                if (mRadioBtn.getText().toString().contains(answersArrayList1.get(2))){
                                    score = score + 5;
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was correct", Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was wrong", Toast.LENGTH_LONG).show();
                                break;

                            case 3:
                                if (mRadioBtn.getText().toString().contains(answersArrayList4.get(3))){
                                    score = score + 5;
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was correct", Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(QuizQuestionsActivity.this,"Your answer was wrong", Toast.LENGTH_LONG).show();
                                break;
                        }

                        mScore.setText(String.valueOf(score));
                        i = i+1;
                        if (i<4){
                            mQuestions.setText(questionsArrayList.get(i));
                            mRadioBtn1.setText(answersArrayList1.get(i));
                            mRadioBtn2.setText(answersArrayList2.get(i));
                            mRadioBtn3.setText(answersArrayList3.get(i));
                            mRadioBtn4.setText(answersArrayList4.get(i));
                        }
                        else{
                            completeDialog.setContentView(R.layout.activity_game_completed);
                            mDone = completeDialog.findViewById(R.id.game_done);
                            mFinalScore = completeDialog.findViewById(R.id.final_score);
                            mViewPoints = completeDialog.findViewById(R.id.view_points);
                            completeDialog.show();
                            mFinalScore.setText(String.valueOf(score));
                            totalScore = score + Integer.parseInt(Objects.requireNonNull(points.getValue()).toString());
                            DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("Points");
                            dBRef.setValue(totalScore);
                            completeGame();
                        }
                    }
                    else{
                        Toast.makeText(QuizQuestionsActivity.this,"Provide an answer to proceed", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void completeGame() {
        mViewPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizQuestionsActivity.this, CustomerProfileActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizQuestionsActivity.this, CustomerGameActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void setNavigation() {
//        Initialize variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_game);
//        Item Selected Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), CustomerServiceActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_favourite:
                        startActivity(new Intent(getApplicationContext(), CustomerFavoritesActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_game:
                        return true;
                    case R.id.navigation_profile:
                        startActivity(new Intent(getApplicationContext(), CustomerProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void getDBSnapshot(){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference();

        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userId = FirebaseAuth.getInstance().getUid();
                points = dataSnapshot.child("Users").child("Customers").child(userId).child("Points");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}