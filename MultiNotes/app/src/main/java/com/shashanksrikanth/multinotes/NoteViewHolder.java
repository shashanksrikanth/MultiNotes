package com.shashanksrikanth.multinotes;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder{
    public TextView noteListTitle;
    public TextView noteListText;
    public TextView noteListDate;

    public NoteViewHolder(View view) {
        super(view);
        noteListTitle = view.findViewById(R.id.noteListTitle);
        noteListText = view.findViewById(R.id.noteListText);
        noteListDate = view.findViewById(R.id.noteListDate);
    }
}
