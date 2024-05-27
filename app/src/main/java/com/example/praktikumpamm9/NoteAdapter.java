package com.example.praktikumpamm9;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> noteList;
    private Context context;
    private DatabaseReference databaseReference;

    public NoteAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance("https://pamfirebase-5056a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes");
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tvDate.setText(note.getDate());
        holder.tvTitle.setText(note.getTitle());
        holder.tvDescription.setText(note.getDescription());

        holder.btnEdit.setOnClickListener(v-> {
            Intent I = new Intent(context, InsertNoteActivity.class);
            I.putExtra("noteId", note.getNoteId());
            I.putExtra("title", note.getTitle());
            I.putExtra("desc", note.getDescription());
            context.startActivity(I);
        });

        holder.btnDelete.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        databaseReference.child(uid).child(note.getNoteId()).removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                            if (noteList.size() > 0 && position < noteList.size()) {
                                noteList.remove(position);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete Note", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle, tvDescription;
        Button btnEdit, btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

}
