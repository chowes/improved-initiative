package me.colinhowes.rollinitiative.data;

import android.provider.BaseColumns;

/**
 * Created by colin on 4/30/17.
 */

public final class CharacterContract {
    public static class CharacterEntry implements BaseColumns {
        public static final String TABLE_NAME = "character_table";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COLOUR = "colour";
        public static final String COLUMN_NAME_HP = "hp";
        public static final String COLUMN_NAME_INIT_BONUS = "init_bonus";
        public static final String COLUMN_NAME_INIT = "init";
        public static final String COLUMN_NAME_CONDITION = "condition";
        public static final String COLUMN_NAME_IN_COMBAT = "in_combat";
    }
}
