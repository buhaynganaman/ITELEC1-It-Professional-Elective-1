package com.example.sqlitenotesapp;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etTitle, etContent;
    private Button btnSave;
    private ListView listNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);
        listNotes = findViewById(R.id.list_notes);

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> saveNote());
        loadNotes();
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.insertNote(title, content);
        if (id != -1) {
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
            etTitle.setText("");
            etContent.setText("");
            loadNotes();
        } else {
            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotes() {
        List<String> notes = dbHelper.getAllNotes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listNotes.setAdapter(adapter);
    }
}
