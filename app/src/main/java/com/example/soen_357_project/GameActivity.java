package com.example.soen_357_project;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextInputEditText answerField;
    private TextView questionText;
    private Button submitButton;
    private List<String> questionsList;
    private List<String> answersList;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, AlertReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        setContentView(R.layout.activity_game);
        questionText=findViewById(R.id.questionTextView);
        answerField=findViewById(R.id.answerTextView);
        submitButton=findViewById(R.id.submitButton);
        setSubmitButton();

        firestore = FirebaseFirestore.getInstance();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("M-d-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        System.out.println(formattedDate);
        firestore.collection("Quiz").document(formattedDate).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()){

                        questionsList = (List<String>) doc.get("Questions");
                        answersList = (List<String>) doc.get("Answers");
                        String question=questionsList.remove(0);
                        questionText.setText(question);
                        questionsList.add(question);

                    }else {
                        finish();
                    }
                }else
                {
                    //Toast.makeText(SplashActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setSubmitButton(){
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("In onclick");
                    if(answerField.getText().toString()!=""){
                        System.out.println(answerField.getText().toString());
                        String answer = answersList.remove(0);
                        answersList.add(answer);
                        if(answerField.getText().toString().equals(answer)){
                            System.out.println("In equal");

                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);

                            // Stops the alarm defined in the AlarmSoundService Class
                            stopService(new Intent(GameActivity.this, AlarmSoundService.class));

                            // Putting the Saved alarm inside a shared preference
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.AlarmTimeFile), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.AlarmTime), "Alarm Stopped");
                            editor.apply();

                            Intent mainIntent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(mainIntent);
                        }
                        else{

                            if(!questionsList.isEmpty())
                            {
                                questionText.setText(questionsList.remove(0));
                                answerField.setText("");
                            }
                            else{
                                Intent mainIntent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(mainIntent);
                            }

                        }
                    }

            }
        });
    }
}