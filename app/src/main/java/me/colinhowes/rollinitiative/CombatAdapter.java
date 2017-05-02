package me.colinhowes.rollinitiative;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.colinhowes.rollinitiative.data.CharacterContract;

/**
 * Created by colin on 4/30/17.
 */

public class CombatAdapter extends RecyclerView.Adapter<CombatAdapter.CombatAdapterViewHolder> {

    final private CharacterClickListener clickListener;
    private Cursor characterCursor;
    private Context context;

    public CombatAdapter(Context context, Cursor cursor, CharacterClickListener listener) {
        this.characterCursor = cursor;
        this.clickListener = listener;
        this.context = context;
    }

    public interface CharacterClickListener {
        void onCharacterClick(int indexClicked);
    }

    @Override
    public CombatAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.character_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        CombatAdapterViewHolder viewHolder = new CombatAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CombatAdapterViewHolder holder, int position) {
        if (!characterCursor.moveToPosition(position)) {
            return;
        }
        int characterId = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry._ID));
        String characterName = characterCursor.getString(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_NAME));
        String characterConditions = characterCursor.getString(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION));
        int characterHitPointCurrent = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
        int characterHitPointTotal = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL));
        int characterInitiative = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT));
        String characterColour = characterCursor.getString(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR));

        // TODO: Should be using string resources here, fix this.
        holder.itemView.setTag(characterId);
        holder.characterName.setText(characterName);
        holder.characterHitPoints.setText("HP: " + characterHitPointCurrent + "/" + characterHitPointTotal);
        holder.characterConditions.setText("Condition:\n" + characterConditions);
        holder.characterInitiative.setText("Init: " + characterInitiative);

        switch (characterColour) {
            case "red":
                holder.characterColour.setImageResource(R.drawable.circle_red);
                break;
            case "blue":
                holder.characterColour.setImageResource(R.drawable.circle_blue);
                break;
            case "yellow":
                holder.characterColour.setImageResource(R.drawable.circle_yellow);
                break;
            case "green":
                holder.characterColour.setImageResource(R.drawable.circle_green);
                break;
            case "black":
                holder.characterColour.setImageResource(R.drawable.circle_black);
                break;
            default:
                holder.characterColour.setImageResource(R.drawable.circle_black);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return characterCursor.getCount();
    }

    class CombatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView characterName;
        TextView characterHitPoints;
        TextView characterConditions;
        TextView characterInitiative;
        ImageView characterColour;

        public CombatAdapterViewHolder(View itemView) {
            super(itemView);

            characterName = (TextView) itemView.findViewById(R.id.tv_character_name);
            characterHitPoints = (TextView) itemView.findViewById(R.id.tv_hit_points);
            characterConditions = (TextView) itemView.findViewById(R.id.tv_conditions);
            characterInitiative = (TextView) itemView.findViewById(R.id.tv_init);
            characterColour = (ImageView) itemView.findViewById(R.id.iv_character_colour);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onCharacterClick(clickedPosition);
        }
    }

}
