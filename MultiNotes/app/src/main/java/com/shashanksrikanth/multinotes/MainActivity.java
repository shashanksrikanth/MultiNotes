package com.shashanksrikanth.multinotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int EDIT_ACTIVITY_REQUEST_CODE = 1;
    private final ArrayList<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    NoteAdapter adapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set recycler view
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NoteAdapter(notes, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Load data from JSON into notes
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Write data from notes into JSON file
        writeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Logic that inflates the menu layout
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Logic that dictates what should be done when a menu item is clicked
        switch(item.getItemId()) {
            case R.id.addNote:
                Intent editIntent = new Intent(this, EditActivity.class);
                editIntent.putExtra("newNote", true);
                startActivityForResult(editIntent, EDIT_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.infoApp:
                Intent infoIntent = new Intent(this, AboutActivity.class);
                startActivity(infoIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Logic that catches the data returned by EditActivity and processes it
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_ACTIVITY_REQUEST_CODE) {
            if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cannot save note with empty title", Toast.LENGTH_LONG).show();
            }
            else if(resultCode == RESULT_OK && data.hasExtra("ValidNote")) {
                // This is a brand new note
                Note note = (Note) data.getSerializableExtra("ValidNote");
                notes.add(note);
                Collections.sort(notes);
                adapter.notifyDataSetChanged();
                changeTitleBar();
            }
            else if(resultCode == RESULT_OK && data.hasExtra("ValidEditNote")) {
                // This is an edited note
                Note note = (Note) data.getSerializableExtra("ValidEditNote");
                int index = data.getIntExtra("EditNoteIndex", 0);
                notes.set(index, note);
                Collections.sort(notes);
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onClick(View v) {
        // Edit a note
        int index = recyclerView.getChildLayoutPosition(v);
        Note note = notes.get(index);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("noteTitle", note.getTitle());
        intent.putExtra("noteText", note.getText());
        intent.putExtra("noteIndex", index);
        intent.putExtra("newNote", false);
        startActivityForResult(intent, EDIT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public boolean onLongClick(View v) {
        // Delete the note after user approval
        final int index = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notes.remove(index);
                adapter.notifyDataSetChanged();
                changeTitleBar();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;
            }
        });
        builder.setMessage("Delete note '" + notes.get(index).getTitle() + "'?");
        builder.setTitle("Delete Confirmation");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    public void changeTitleBar() {
        // Change title bar to reflect number of notes
        ActionBar titleBar = getSupportActionBar();
        String titleBarText = getString(R.string.app_name) + " (" + notes.size() + ")";
        titleBar.setTitle(titleBarText);
    }

    public void writeData() {
        // Writing data from notes to JSON file
        try {
            FileOutputStream fs = getApplicationContext().openFileOutput(getString(R.string.notes_file), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fs, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note n : notes) {
                writer.beginObject();
                writer.name("noteTitle").value(n.getTitle());
                writer.name("noteText").value(n.getText());
                writer.name("noteLastEdit").value(n.getLastEdit().getTime());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeData: " + e.getMessage());
        }
    }

    public void loadData() {
        // Writing JSON data to notes
        try {
            FileInputStream fs = getApplicationContext().openFileInput(getString(R.string.notes_file));
            byte[] data = new byte[(int) fs.available()];
            int loaded = fs.read(data);
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fs.close();
            String json = new String(data);
            JSONArray noteArray = new JSONArray(json);
            for (int i = 0; i < noteArray.length(); i++) {
                JSONObject noteObject = noteArray.getJSONObject(i);
                String title = noteObject.getString("noteTitle");
                String text = noteObject.getString("noteText");
                long lastEdit = noteObject.getLong("noteLastEdit");
                Note n = new Note(title, text);
                n.setLastEdit(lastEdit);
                notes.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "loadData: " + e.getMessage());
        }
    }
}