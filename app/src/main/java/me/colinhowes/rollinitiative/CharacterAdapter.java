package me.colinhowes.rollinitiative;

import android.content.Context;
import android.database.Cursor;
import android.nfc.Tag;
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

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterAdapterViewHolder> {

    private Cursor characterCursor;
    private CharacterAdapter.CharacterClickListener clickListener;
    private Context context;

    public CharacterAdapter(Context context, Cursor cursor, CharacterAdapter.CharacterClickListener listener) {
        this.characterCursor = cursor;
        this.clickListener = listener;
        this.context = context;
    }

    public Cursor changeCursor(Cursor newCursor) {
        if (characterCursor == newCursor) {
            return null;
        }

        Cursor temp = characterCursor;
        characterCursor = newCursor;

        if (characterCursor != null) {
            notifyDataSetChanged();
        }
        return temp;
    }

    public interface CharacterClickListener {
        void onCharacterClick(int characterId);
    }

    @Override
    public CharacterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.character_select_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        CharacterAdapterViewHolder viewHolder = new CharacterAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CharacterAdapterViewHolder holder, int position) {
        if (!characterCursor.moveToPosition(position)) {
            return;
        }
        int characterId = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry._ID));
        String characterName = characterCursor.getString(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_NAME));
        int characterHitPointCurrent = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
        int characterHitPointTotal = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL));
        int characterInitBonus = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS));
        String characterColour = characterCursor.getString(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR));
        int characterInCombat = characterCursor.getInt(
                characterCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT));

        // TODO: Should be using string resources here, fix this.
        holder.itemView.setTag(characterId);
        holder.characterName.setText(characterName);
        holder.characterHitPoints.setText("HP: " + characterHitPointCurrent + "/" + characterHitPointTotal);
        holder.characterInitBonus.setText("Init. Bonus: " + characterInitBonus);

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

        if (characterInCombat == 1) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources()
                    .getColor(R.color.colorActiveTurn));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources()
                    .getColor(R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        if (characterCursor != null) {
            return characterCursor.getCount();
        } else {
            return 0;
        }
    }

    class CharacterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView characterName;
        TextView characterHitPoints;
        TextView characterInitBonus;
        ImageView characterColour;

        public CharacterAdapterViewHolder(View itemView) {
            super(itemView);

            characterName = (TextView) itemView.findViewById(R.id.tv_name_select);
            characterHitPoints = (TextView) itemView.findViewById(R.id.tv_hp_select);
            characterInitBonus = (TextView) itemView.findViewById(R.id.tv_init_bonus_select);
            characterColour = (ImageView) itemView.findViewById(R.id.iv_color_select);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int characterId = (Integer) v.getTag();
            clickListener.onCharacterClick(characterId);
        }
    }

}
