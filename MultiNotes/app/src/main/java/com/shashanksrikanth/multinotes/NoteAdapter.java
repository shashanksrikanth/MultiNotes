package com.shashanksrikanth.multinotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder>{
    private ArrayList<Note> notes;
    private MainActivity mainActivity;

    public NoteAdapter(ArrayList<Note> notes, MainActivity mainActivity) {
        this.notes = notes;
        this.mainActivity = mainActivity;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View noteView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);
        noteView.setOnClickListener(mainActivity);
        noteView.setOnLongClickListener(mainActivity);
        NoteViewHolder holder = new NoteViewHolder(noteView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int index) {
        Note note = notes.get(index);
        String noteTitle = note.getTitle();
        String noteText = note.getText();
        Date noteLastEdit = note.getLastEdit();
        holder.noteListTitle.setText(noteTitle);
        if(noteText.length()>80) {
            String newNoteText = noteText.substring(0,81)+"...";
            holder.noteListText.setText(newNoteText);
        }
        else holder.noteListText.setText(noteText);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm a", Locale.ENGLISH);
        sdf.applyPattern("EEE MMM dd, KK:mm a");
        holder.noteListDate.setText(sdf.format(noteLastEdit));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
