package me.colinhowes.rollinitiative;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import me.colinhowes.rollinitiative.data.CharacterType;

import static me.colinhowes.rollinitiative.CombatAdapter.CharacterStatus.ACTIVE;
import static me.colinhowes.rollinitiative.CombatAdapter.CharacterStatus.DELAY;
import static me.colinhowes.rollinitiative.CombatAdapter.CharacterStatus.INACTIVE;
import static me.colinhowes.rollinitiative.CombatAdapter.CombatClickListener.EventType.DECREASE_HEALTH;
import static me.colinhowes.rollinitiative.CombatAdapter.CombatClickListener.EventType.INCREASE_HEALTH;
import static me.colinhowes.rollinitiative.CombatAdapter.CombatClickListener.EventType.ITEM_CLICK;

/**
 * Created by colin on 4/30/17.
 */

public class CombatAdapter extends RecyclerView.Adapter<CombatAdapter.CombatAdapterViewHolder> {

    private ArrayList<CharacterType> characterList;
    final private CombatClickListener clickListener;


    enum CharacterStatus {
        ACTIVE,
        INACTIVE,
        DELAY
    }


    public CombatAdapter(ArrayList<CharacterType> characterList, CombatClickListener listener) {
        this.characterList = characterList;
        this.clickListener = listener;
    }


    public interface CombatClickListener {
        enum EventType {
            INCREASE_HEALTH,
            DECREASE_HEALTH,
            ITEM_CLICK
        }
        void onCombatClick(int position, EventType eventType);
    }


    public ArrayList<CharacterType> changeList(ArrayList<CharacterType> newList) {
        if (characterList == newList) {
            return null;
        }

        ArrayList<CharacterType> temp = characterList;
        characterList = newList;

        if (characterList != null) {
            notifyDataSetChanged();
        }

        return temp;
    }


    public boolean swapCharacters(int fromIndex, int toIndex) {

        CharacterType character;

        if (characterList.isEmpty()) {
            return false;
        }
        if (fromIndex < toIndex) {
            for (int i = fromIndex; i < toIndex; i++) {
                character = characterList.remove(i);
                characterList.add(i + 1, character);
                // this gives us the animation
                notifyItemMoved(i, i + 1);
            }
            notifyItemRangeChanged(fromIndex, toIndex - fromIndex + 1);
        } else {
            for (int i = fromIndex; i > toIndex; i--) {
                character = characterList.remove(i);
                characterList.add(i - 1, character);
                // this gives us the animation
                notifyItemMoved(i, i - 1);
            }
            notifyItemRangeChanged(toIndex, fromIndex - toIndex + 1);
        }

        return true;
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
        CharacterType character;
        try {
            character = characterList.get(position);
        } catch (IndexOutOfBoundsException e) {
            Log.e("onBindViewHolder", "Index is out of bounds!");
            return;
        }



        // This tag is used to get the character ID when we want to update the database
        holder.itemView.setTag(position);
        holder.plusButton.setTag(position);
        holder.minusButton.setTag(position);

        // TODO: Should be using string resources here, fix this.
        holder.characterName.setText(character.getName());
        holder.characterHitPoints.setText(holder.itemView.getResources().
                getString(R.string.char_hp, character.getHpCurrent(), character.getHpTotal()));
        holder.characterInitBonus.setText(holder.itemView.getResources().
                getString(R.string.char_init_bonus, character.getInitBonus()));
        holder.characterInitScore.setText(holder.itemView.getResources().
                getString(R.string.char_init_score, character.getInit()));

        // Set the colour - consider changing drawable to something that can be coloured dynamically
        switch (character.getColour()) {
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
            case "Purple":
                holder.characterColour.setImageResource(R.drawable.circle_purple);
                break;
            case "Orange":
                holder.characterColour.setImageResource(R.drawable.circle_orange);
                break;
            case "Brown":
                holder.characterColour.setImageResource(R.drawable.circle_brown);
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
            character.setDelayTurn(0);
            setActiveCharacter(holder, ACTIVE);
        } else if (character.getDelayTurn() == 1) {
            setActiveCharacter(holder, DELAY);
        } else {
            setActiveCharacter(holder, INACTIVE);
        }
    }





    /*
    * Set the background of the given ViewHolder to green (active) or white (inactive)
    */
    private void setActiveCharacter(RecyclerView.ViewHolder viewHolder, CharacterStatus status) {

        // Highlight the character if it is in combat
        if (viewHolder == null) {
            Log.e("setActiveCharacter", "null viewHolder");
            return;
        }
        if (status == ACTIVE) {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getResources()
                    .getColor(R.color.colorActiveTurn));
        } else if (status == DELAY) {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getResources()
                    .getColor(R.color.colorDelayTurn));
        } else {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getResources()
                    .getColor(R.color.white));
        }

    }


    @Override
    public int getItemCount() {
        if (characterList != null) {
            // We need this check since we do our load asynchronously
            return characterList.size();
        } else {
            return 0;
        }
    }


    public ArrayList<CharacterType> getCharacterList() {
        return characterList;
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
            int position = (Integer) v.getTag();
            CombatClickListener.EventType eventType;

            if (v.getId() == R.id.ib_hp_minus_select) {
                eventType = DECREASE_HEALTH;
            } else if (v.getId() == R.id.ib_hp_add_select) {
                eventType = INCREASE_HEALTH;
            } else {
                eventType = ITEM_CLICK;
            }
            clickListener.onCombatClick(position, eventType);
        }
    }

}
