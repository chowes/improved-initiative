package me.colinhowes.rollinitiative;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.colinhowes.rollinitiative.data.CharacterContract;

import static me.colinhowes.rollinitiative.CombatAdapter.CombatClickListener.EventType.DECREASE_HEALTH;
import static me.colinhowes.rollinitiative.CombatAdapter.CombatClickListener.EventType.INCREASE_HEALTH;
import static me.colinhowes.rollinitiative.CombatAdapter.CombatClickListener.EventType.ITEM_CLICK;

/**
 * Created by colin on 4/30/17.
 */

public class CombatAdapter extends RecyclerView.Adapter<CombatAdapter.CombatAdapterViewHolder> {

    private Cursor combatCursor;
    final private CombatClickListener clickListener;
    private Context context;

    public CombatAdapter(Context context, Cursor cursor, CombatClickListener listener) {
        this.combatCursor = cursor;
        this.clickListener = listener;
        this.context = context;
    }

    public interface CombatClickListener {
        enum EventType {
            INCREASE_HEALTH,
            DECREASE_HEALTH,
            ITEM_CLICK
        }
        void onCombatClick(int characterId, EventType eventType);
    }

    public Cursor changeCursor(Cursor newCursor) {
        if (combatCursor == newCursor) {
            return null;
        }

        Cursor temp = combatCursor;
        combatCursor = newCursor;

        if (combatCursor != null) {
            notifyDataSetChanged();
        }
        if (temp != null) {
            temp.close();
        }

        return temp;
    }

    @Override
    public CombatAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.character_select_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        return new CombatAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CombatAdapterViewHolder holder, int position) {
        if (!combatCursor.moveToPosition(position)) {
            // something probably went wrong when we manipulated the database
            Log.e("onBindViewHolder", "Cursor returned null on move to position");
            return;
        }

        int characterId = combatCursor.getInt(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry._ID));
        String characterName = combatCursor.getString(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_NAME));
        int characterHitPointCurrent = combatCursor.getInt(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
        int characterHitPointTotal = combatCursor.getInt(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL));
        int characterInitBonus = combatCursor.getInt(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS));
        int characterInitScore = combatCursor.getInt(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT));
        String characterColour = combatCursor.getString(
                combatCursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR));

        // This tag is used to get the character ID when we want to update the database
        holder.itemView.setTag(characterId);
        holder.plusButton.setTag(characterId);
        holder.minusButton.setTag(characterId);

        // TODO: Should be using string resources here, fix this.
        holder.characterName.setText(characterName);
        holder.characterHitPoints.setText("HP: " + characterHitPointCurrent + "/" + characterHitPointTotal);
        holder.characterInitBonus.setText("Bonus: " + characterInitBonus);
        holder.characterInitScore.setText("Score: " + characterInitScore);

        // Set the colour - consider changing drawable to something that can be coloured dynamically
        switch (characterColour) {
            case "Red":
                holder.characterColour.setImageResource(R.drawable.circle_red);
                break;
            case "Blue":
                holder.characterColour.setImageResource(R.drawable.circle_blue);
                break;
            case "Yellow":
                holder.characterColour.setImageResource(R.drawable.circle_yellow);
                break;
            case "Green":
                holder.characterColour.setImageResource(R.drawable.circle_green);
                break;
            case "Black":
                holder.characterColour.setImageResource(R.drawable.circle_black);
                break;
            default:
                holder.characterColour.setImageResource(R.drawable.circle_black);
                break;
        }

        // Highlight the character if it is in combat
        if (position == 0) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources()
                    .getColor(R.color.colorActiveTurn));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources()
                    .getColor(R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        if (combatCursor != null) {
            // We need this check since we do our load asynchronously
            return combatCursor.getCount();
        } else {
            return 0;
        }
    }

    class CombatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView characterName;
        TextView characterHitPoints;
        TextView characterInitBonus;
        TextView characterInitScore;
        ImageView characterColour;
        ImageButton minusButton;
        ImageButton plusButton;

        public CombatAdapterViewHolder(View itemView) {
            super(itemView);

            characterName = (TextView) itemView.findViewById(R.id.tv_name_select);
            characterHitPoints = (TextView) itemView.findViewById(R.id.tv_hp_select);
            characterInitBonus = (TextView) itemView.findViewById(R.id.tv_init_bonus_select);
            characterInitScore = (TextView) itemView.findViewById(R.id.tv_init_score_select);
            characterColour = (ImageView) itemView.findViewById(R.id.iv_color_select);
            minusButton = (ImageButton) itemView.findViewById(R.id.ib_hp_minus_select);
            plusButton = (ImageButton) itemView.findViewById(R.id.ib_hp_add_select);

            // we use onClick to dispatch to the CombatClickListener
            itemView.setOnClickListener(this);
            minusButton.setOnClickListener(this);
            plusButton.setOnClickListener(this);
        }

        /*
         * Dispatcher for character click events
         * Clicks are handled in CharacterActivity
         */
        @Override
        public void onClick(View v) {

            // All of the pressable elements have the character ID as a tag
            int characterId = (Integer) v.getTag();
            CombatClickListener.EventType eventType;

            if (v.getId() == R.id.ib_hp_minus_select) {
                eventType = DECREASE_HEALTH;
            } else if (v.getId() == R.id.ib_hp_add_select) {
                eventType = INCREASE_HEALTH;
            } else {
                eventType = ITEM_CLICK;
            }
            clickListener.onCombatClick(characterId, eventType);
        }
    }

}
