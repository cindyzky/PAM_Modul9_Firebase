package com.example.praktikumpamm9;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InsertNoteActivity extends AppCompatActivity {
    private EditText etTitle, etDesc;
    private Button btnSubmit;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_description);
        btnSubmit = findViewById(R.id.btn_submit);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://pamfirebase-5056a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes");

        Intent I = getIntent();
        if (I.hasExtra("noteId")) {
            noteId = I.getStringExtra("noteId");
            etTitle.setText(I.getStringExtra("title"));
            etDesc.setText(I.getStringExtra("description"));
        } else {
            noteId = null;
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    public void submitData(){
        if (!validateForm()){
            return;
        }
        String currentDate = getCurrentDate();
        String title = etTitle.getText().toString();
        String desc = etDesc.getText().toString();
        Note newNote = new Note(currentDate, title, desc);

        if (noteId == null) {
            // New note
            databaseReference.child(mAuth.getUid()).push().setValue(newNote)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(InsertNoteActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(InsertNoteActivity.this, "Failed to add note", Toast.LENGTH_SHORT).show());
        } else {
            databaseReference.child(mAuth.getUid()).child(noteId).setValue(newNote)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(InsertNoteActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(InsertNoteActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show());
        }

    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            etTitle.setError("Required");
            result = false;
        } else {
            etTitle.setError(null);
        }
        if (TextUtils.isEmpty(etDesc.getText().toString())) {
            etDesc.setError("Required");
            result = false;
        } else {
            etDesc.setError(null);
        }
        return result;
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}