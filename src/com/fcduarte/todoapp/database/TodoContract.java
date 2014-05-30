package com.fcduarte.todoapp.database;

import android.provider.BaseColumns;

public final class TodoContract {

	public TodoContract() {
	}

	public static abstract class TodoItem implements BaseColumns {
		public static final String TABLE_NAME = "items";
		public static final String COLUMN_NAME_ITEM_DESCRIPTION = "description";
		public static final int COLUMN_NAME_ID_POSITION = 0;
		public static final int COLUMN_NAME_ITEM_DESCRIPTION_POSITION = 1;
	}

	public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ TodoItem.TABLE_NAME + " (" + TodoItem._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + TodoItem.COLUMN_NAME_ITEM_DESCRIPTION
			+ " TEXT)";
	
	public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TodoItem.TABLE_NAME;

}
