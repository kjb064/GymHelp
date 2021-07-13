package com.example.android.gymhelp;

import android.view.Menu;

public class Constants {
    public static final int CHEST = 0;
    public static final int LEGS = 1;
    public static final int BACK = 2;
    public static final int SHOULDERS = 3;
    public static final int ARMS = 4;
    public static final int ABS = 5;
    public static final int COMPOUND = 6;

    public static final int NUM_TARGETS = 7;
    public static final String NO_IMAGE_PROVIDED = "NONE";
    public static final String DEFAULT_DATE = "--";

    /** ID of the "Read Full" item in the context menu. */
    public static final int READ_FULL_ID = Menu.FIRST;
    /** ID of the "Delete" item in the context menu. */
    public static final int DELETE_ID = Menu.FIRST + 1;
    /** ID of the "Edit" item in the context menu. */
    public static final int EDIT_ID = Menu.FIRST + 2;
    /** ID of the "Set/Unset Weight Flag" item in the context menu. */
    public static final int WEIGHT_FLAG_ID = Menu.FIRST + 3;
}
