package com.example.praktikumpamm9;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReadNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvEmail, tvUid;
    private Button btnKeluar;
    private FirebaseAuth mAuth;
    private FloatingActionButton btnAdd;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEmail = findViewById(R.id.tv_email);
        tvUid = findViewById(R.id.tv_uid);
        btnKeluar = findViewById(R.id.btn_keluar);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.rv_notes);

        mAuth = FirebaseAuth.getInstance();

        btnKeluar.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        fetchData();

    }

    private void fetchData() {
        databaseReference = FirebaseDatabase.getInstance("https://pamfirebase-5056a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes").child(mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    note.setNoteId(dataSnapshot.getKey());
                    noteList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReadNoteActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
            tvUid.setText(currentUser.getUid());
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_keluar) {
            logOut();
        } else if (v.getId() == R.id.btn_add) {
            Intent intent = new Intent(ReadNoteActivity.this, InsertNoteActivity.class);
            startActivity(intent);
        }
    }

    public void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(ReadNoteActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // ensures user can't go back
        startActivity(intent);
    }


}