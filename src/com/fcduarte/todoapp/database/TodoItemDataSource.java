package com.fcduarte.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fcduarte.todoapp.database.TodoContract.TodoItem;

public class TodoItemDataSource {

	private SimpleTodoDb dbHelper;
	private SQLiteDatabase database;
	
	public TodoItemDataSource(Context context) {
		dbHelper = new SimpleTodoDb(context);
	}
	
	public void open() {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void create(String description) {
		ContentValues values = new ContentValues();
		values.put(TodoItem.COLUMN_NAME_ITEM_DESCRIPTION, description);

		database.insert(TodoItem.TABLE_NAME, null, values);
	}
	
	public void update(Long itemId, String newDescription) {
		ContentValues values = new ContentValues();
		values.put(TodoItem.COLUMN_NAME_ITEM_DESCRIPTION, newDescription);

		String selection = TodoItem._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(itemId) };

		database.update(TodoItem.TABLE_NAME, values, selection, selectionArgs);	
	}
	
	public void delete(Long itemId) {
		String selection = TodoItem._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(itemId) };
		
		database.delete(TodoItem.TABLE_NAME, selection, selectionArgs);
	}
	
	public Cursor getAll() {
		String[] projection = {
		    TodoItem._ID,
		    TodoItem.COLUMN_NAME_ITEM_DESCRIPTION
		    };

		Cursor cursor = database.query(TodoItem.TABLE_NAME, projection, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		return cursor; 
	}

}
