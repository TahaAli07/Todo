package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.todo.Adapters.NotesListAdapter;
import com.example.todo.Database.Note;
import com.example.todo.ViewModels.NoteViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListAdapter.clickListener {
    private NoteViewModel noteViewModel;
    private Toolbar myToolbar;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.homeLinearLayout);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final NotesListAdapter notesListAdapter = new NotesListAdapter(this, this);
        recyclerView.setAdapter(notesListAdapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesListAdapter.setNotesList(notes);
                Toast.makeText(MainActivity.this, "onChanged", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                showAddNoteAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void showAddNoteAlertDialog() {

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_note_alert_dialog, viewGroup, false);
        final EditText titleET = dialogView.findViewById(R.id.titleEditText);
        final EditText descET = dialogView.findViewById(R.id.descEditText);
        Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
        Button submitBtn = dialogView.findViewById(R.id.submitBtn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Add Note");
        builder.setView(dialogView);

        final AlertDialog addNoteAlertDialog = builder.create();
        addNoteAlertDialog.show();

        final int[] priority = {0};
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString().trim();
                String description = descET.getText().toString().trim();
                if (title.isEmpty()) {
                    addNoteAlertDialog.dismiss();
                    makeSnackbar("Please enter a title");
                    return;
                }
                if (description.isEmpty()) {
                    description = "description";
                }
                Note note = new Note(title, description, priority[0], 0);
                noteViewModel.insert(note);
                addNoteAlertDialog.dismiss();
                makeSnackbar("Task added to your list");
            }
        });
    }

    public void makeSnackbar(String msg) {
        Snackbar.make(linearLayout, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDelete(Note note) {
        noteViewModel.delete(note);
        makeSnackbar("Note deleted");
    }

    @Override
    public void onUndo(Note note) {
        noteViewModel.update(note);
        makeSnackbar("Note marked as incomplete");
    }

    @Override
    public void onCheck(Note note) {
        noteViewModel.update(note);
        makeSnackbar("Note marked as complete");
    }
}