package com.fcduarte.todoapp.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fcduarte.todoapp.R;
import com.fcduarte.todoapp.database.TodoContract.TodoItem;
import com.fcduarte.todoapp.database.TodoItemDataSource;
import com.fcduarte.todoapp.util.SwipeDismissListViewTouchListener;

public class TodoActivity extends Activity {

	public static int RESULT_OK = 200;
	public static int RESULT_NOT_MODIFIED = 304;
	private static int REQUEST_EDIT_ITEM = 0;

	private static TodoItemDataSource datasource;
	private static SimpleCursorAdapter itemsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
		}
		
		datasource = new TodoItemDataSource(this);
		datasource.open();
		
		itemsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, 
				datasource.getAll(), new String[] { TodoItem.COLUMN_NAME_ITEM_DESCRIPTION }, 
				new int[] { android.R.id.text1 }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_EDIT_ITEM) {
			Long itemId = data.getLongExtra(EditItemActivity.ITEM_ID, -1);
			String newItemDescription = data.getStringExtra(EditItemActivity.ITEM_DESCRIPTION);
			updateItem(itemId, newItemDescription);
		}
	}

	public static class MainFragment extends Fragment {

		private ListView lvItems;
		private View rootView;
		private Button btnAddItem;
		private EditText etNewItem;


		public MainFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_todo, container, false);
			etNewItem = (EditText) rootView.findViewById(R.id.etNewItem);

			lvItems = (ListView) rootView.findViewById(R.id.lvItems);
			lvItems.setAdapter(itemsAdapter);
			
			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
					lvItems,
					new SwipeDismissListViewTouchListener.DismissCallbacks() {
						@Override
						public boolean canDismiss(int position) {
							return true;
						}

						@Override
						public void onDismiss(ListView listView, int[] reverseSortedPositions) {
							for (int position : reverseSortedPositions) {
								removeTodoItem(position);
							}
						}
					});
			
	        lvItems.setOnTouchListener(touchListener);
	        lvItems.setOnScrollListener(touchListener.makeScrollListener());

			lvItems.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Cursor cursor = itemsAdapter.getCursor();
					
					Intent intent = new Intent(getActivity(), EditItemActivity.class);
					intent.putExtra(EditItemActivity.ITEM_ID, 
							cursor.getLong(TodoItem.COLUMN_NAME_ID_POSITION));
					intent.putExtra(EditItemActivity.ITEM_DESCRIPTION, 
							cursor.getString(TodoItem.COLUMN_NAME_ITEM_DESCRIPTION_POSITION));
					
					getActivity().startActivityForResult(intent, TodoActivity.REQUEST_EDIT_ITEM);					
				}
			});

			btnAddItem = (Button) rootView.findViewById(R.id.btnAddItem);
			btnAddItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addTodoItem();
					
					etNewItem.setText("");
					lvItems.smoothScrollToPosition(itemsAdapter.getCount());
				}
			});

			return rootView;
		}

		private void addTodoItem() {
			datasource.create(etNewItem.getText().toString());
			itemsAdapter.changeCursor(datasource.getAll());
		}

		private void removeTodoItem(int position) {
			Cursor cursor = itemsAdapter.getCursor();
			cursor.moveToPosition(position);
			
			datasource.delete(cursor.getLong(TodoItem.COLUMN_NAME_ID_POSITION));
			itemsAdapter.changeCursor(datasource.getAll());
		}

	}
	
	private void updateItem(Long itemId, String newItemDescription) {
		datasource.update(itemId, newItemDescription);
		itemsAdapter.changeCursor(datasource.getAll());
	}

}
