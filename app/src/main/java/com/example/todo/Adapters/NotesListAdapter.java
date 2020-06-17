package com.example.todo.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.Database.Note;
import com.example.todo.R;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder> {

    private final LayoutInflater layoutInflater;

    private List<Note> notesList;

    private clickListener listener;

    @NonNull
    @Override
    public NotesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.notes_list_item, parent, false);
        return new NotesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesListViewHolder holder, int position) {
        if (notesList != null) {
            Note currNote = notesList.get(position);
            holder.titleTextView.setText(currNote.getTitle());
            holder.descTextView.setText(currNote.getDescription());
            if (currNote.getIsComplete() == 1) {
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(View.GONE);
                holder.extraOptions.setVisibility(View.VISIBLE);
                //strike through
                holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(false);
                holder.extraOptions.setVisibility(View.GONE);
                //undo strike through
                holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

        } else {
        }
    }

    @Override
    public int getItemCount() {
        if (notesList != null)
            return notesList.size();
        else
            return 0;
    }

    public void setNotesList(List<Note> notes) {
        this.notesList = notes;
        notifyDataSetChanged();
    }

    public interface clickListener {
        void onDelete(Note note);

        void onUndo(Note note);

        void onCheck(Note note);
    }

    public NotesListAdapter(Context context, clickListener listener) {
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public class NotesListViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView titleTextView;
        private final TextView descTextView;
        private final CheckBox checkBox;
        private final LinearLayout extraOptions;
        private final ImageButton undoBtn;
        private final ImageButton deleteBtn;

        private NotesListViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descTextView = itemView.findViewById(R.id.descTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
            extraOptions = itemView.findViewById(R.id.extraOptionLinearLayout);
            undoBtn = itemView.findViewById(R.id.undoBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(notesList.get(getAdapterPosition()));
                }
            });

            undoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.VISIBLE);
                    extraOptions.setVisibility(View.GONE);
                    Note currNote = notesList.get(getAdapterPosition());
                    Note newNote = new Note(currNote.getTitle(), currNote.getDescription(), currNote.getPriority(), currNote.getIsComplete() == 0 ? 1 : 0);
                    newNote.setId(currNote.getId());
                    listener.onUndo(newNote);
                    //undo strike
                    titleTextView.setPaintFlags(titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(true);
                    checkBox.setVisibility(View.GONE);
                    extraOptions.setVisibility(View.VISIBLE);
                    //strike through
                    titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    Note currNote = notesList.get(getAdapterPosition());
                    Note newNote = new Note(currNote.getTitle(), currNote.getDescription(), currNote.getPriority(), currNote.getIsComplete() == 0 ? 1 : 0);
                    newNote.setId(currNote.getId());
                    listener.onCheck(newNote);
                }
            });
        }
    }
}