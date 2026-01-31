package com.example.noteapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAddNote;
    private List<Note> noteList;
    private LinearLayout emptyView;
    private TextView tvNoteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialisation
        recyclerView = findViewById(R.id.recyclerView);
        fabAddNote = findViewById(R.id.fabAddNote);
        emptyView = findViewById(R.id.emptyView);
        tvNoteCount = findViewById(R.id.tvNoteCount);
        databaseHelper = new DatabaseHelper(this);

        // Configuration RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);

        // Charger les notes
        loadNotes();

        // Bouton ajouter
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, AddEditNote.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(); // Recharger les notes quand on revient
    }

    private void loadNotes() {
        noteList = databaseHelper.getAllNotes();
        noteAdapter.updateNotes(noteList);

        // Afficher/cacher le message "aucune note"
        if (noteList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvNoteCount.setText("0 note");
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvNoteCount.setText(noteList.size() + (noteList.size() > 1 ? " notes" : " note"));
        }
    }

    @Override
    public void onNoteClick(Note note) {
        // Ouvrir l'activité pour éditer
        Intent intent = new Intent(home.this, AddEditNote.class);
        intent.putExtra("note_id", note.getId());
        intent.putExtra("note_title", note.getTitle());
        intent.putExtra("note_content", note.getContent());
        intent.putExtra("note_date", note.getDate());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Note note) {
        showDeleteDialog(note);
    }

    private void showDeleteDialog(Note note) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_note);

        // Configurer la largeur du dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85), // 85% de la largeur
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnDelete = dialog.findViewById(R.id.btnDialogDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            databaseHelper.deleteNote(note.getId());
            loadNotes();
            Toast.makeText(this, "Note supprimée", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}