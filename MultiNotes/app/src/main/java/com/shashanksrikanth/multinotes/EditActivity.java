package com.shashanksrikanth.multinotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    TextView noteTitle;
    TextView noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        noteTitle = findViewById(R.id.noteTitle);
        noteText = findViewById(R.id.noteText);
        noteText.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        if(intent.hasExtra("noteTitle")) noteTitle.setText(intent.getStringExtra("noteTitle"));
        if(intent.hasExtra("noteText")) noteText.setText(intent.getStringExtra("noteText"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Logic that inflates the menu layout
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Logic that dictates what should be done when a menu item is clicked
        if(item.getItemId() == R.id.saveNote) {
            String title = noteTitle.getText().toString();
            String text = noteText.getText().toString();
            boolean newNote = getIntent().getBooleanExtra("newNote", true);
            if(title.length()==0 && text.length()==0) {
                Intent data = new Intent();
                Note note = new Note("", "");
                data.putExtra("EmptyNote", note);
                setResult(RESULT_CANCELED, data);
                finish();
            }
            else if(title.length()==0) {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
            else if(newNote) {
                // This is a brand new note
                sendNewNote(title, text);
            }
            else {
                // This is a note that is being edited
                sendEditNote(title, text);
            }
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        boolean newNote = getIntent().getBooleanExtra("newNote", true);
        if(newNote) {
            // If it is a new note, then the dialog should come up anyway
            final String title = noteTitle.getText().toString();
            final String text = noteText.getText().toString();
            if(title.length()!=0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendNewNote(title, text);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data = new Intent();
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                builder.setMessage("Save note '" + noteTitle.getText().toString() + "'?");
                builder.setTitle("Your note is not saved!");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }

        }
        else {
            // If an already created note is clicked on, the code checks if it has been edited. If yes, it pops up a dilaog.
            // If not, it just exits.
            String previousTitle = getIntent().getStringExtra("noteTitle");
            String previousText = getIntent().getStringExtra("noteText");
            final String title = noteTitle.getText().toString();
            final String text = noteText.getText().toString();
            if(title.length()==0) {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
            else if(!title.equals(previousTitle) || !text.equals(previousText) && title.length()!=0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data = new Intent();
                        Note note = new Note(title, text);
                        data.putExtra("ValidEditNote", note);
                        data.putExtra("EditNoteIndex", getIntent().getIntExtra("noteIndex", 0));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent data = new Intent();
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                builder.setMessage("Save note '" + noteTitle.getText().toString() + "'?");
                builder.setTitle("Your note is not saved!");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public void sendNewNote(String title, String text) {
        // Sending data of a new note to MainActivity
        Intent data = new Intent();
        Note note = new Note(title, text);
        data.putExtra("ValidNote", note);
        setResult(RESULT_OK, data);
        finish();
    }

    public void sendEditNote(String title, String text) {
        // Sending data of an edited note to MainActivity
        String previousTitle = getIntent().getStringExtra("noteTitle");
        String previousText = getIntent().getStringExtra("noteText");
        if(!title.equals(previousTitle) || !text.equals(previousText)) {
            Intent data = new Intent();
            Note note = new Note(title, text);
            data.putExtra("ValidEditNote", note);
            data.putExtra("EditNoteIndex", getIntent().getIntExtra("noteIndex", 0));
            setResult(RESULT_OK, data);
            finish();
        }
        else {
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        }
    }
}