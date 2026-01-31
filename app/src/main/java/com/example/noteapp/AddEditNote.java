package com.example.noteapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditNote extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnSave, btnCancel;
    private TextView tvPageTitle;
    private DatabaseHelper databaseHelper;
    private Note currentNote;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        // Initialisation
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        tvPageTitle = findViewById(R.id.tvPageTitle);
        databaseHelper = new DatabaseHelper(this);

        // Vérifier si c'est un mode édition
        if (getIntent().hasExtra("note_id")) {
            isEditMode = true;
            tvPageTitle.setText("Modifier la Note");
            loadNoteData();
        }

        // Bouton Enregistrer
        btnSave.setOnClickListener(v -> saveNote());

        // Bouton Annuler
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadNoteData() {
        int noteId = getIntent().getIntExtra("note_id", -1);
        String title = getIntent().getStringExtra("note_title");
        String content = getIntent().getStringExtra("note_content");
        String date = getIntent().getStringExtra("note_date");

        if (noteId != -1) {
            currentNote = new Note(noteId, title, content, date);
            etTitle.setText(title);
            etContent.setText(content);
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un titre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer du contenu", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        if (isEditMode) {
            // Mettre à jour la note existante
            currentNote.setTitle(title);
            currentNote.setContent(content);
            currentNote.setDate(currentDate);
            databaseHelper.updateNote(currentNote);
            Toast.makeText(this, "Note modifiée avec succès!", Toast.LENGTH_SHORT).show();
        } else {
            // Ajouter une nouvelle note
            Note newNote = new Note(title, content, currentDate);
            databaseHelper.addNote(newNote);
            Toast.makeText(this, "Note ajoutée avec succès!", Toast.LENGTH_SHORT).show();
        }

        finish(); // Retourner à l'activité précédente
    }
}