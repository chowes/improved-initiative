package me.colinhowes.rollinitiative;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by colin on 4/30/17.
 */

public class CombatAdapter extends RecyclerView.Adapter<CombatAdapter.CombatAdapterViewHolder> {

    private int numItems;

    public CombatAdapter(int numItems) {
        this.numItems = numItems;
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
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numItems;
    }

    class CombatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView listItemCharacterView;

        public CombatAdapterViewHolder(View itemView) {
            super(itemView);

            listItemCharacterView = (TextView) itemView.findViewById(R.id.tv_character_item);
        }

        private void bind(int index) {
            listItemCharacterView.setText(String.valueOf(index));
        }


        @Override
        public void onClick(View v) {

        }
    }

}
