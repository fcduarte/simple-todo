package com.fcduarte.todoapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class TodoActivity extends Activity {

	public static int RESULT_OK = 200;
	public static int RESULT_NOT_MODIFIED = 304;
	private static int REQUEST_EDIT_ITEM = 0;

	private static ArrayList<String> items;
	private static ArrayAdapter<String> itemsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
		}

		readItems();
		itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
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
			String newItemDescription = data.getStringExtra(EditItemActivity.ITEM_DESCRIPTION);
			int itemPosition = data.getIntExtra(EditItemActivity.ITEM_POSITION,	-1);

			if (itemPosition != -1) {
				items.set(itemPosition, newItemDescription);
				itemsAdapter.notifyDataSetChanged();
				writeItems();
			}
		}
	}

	public static class MainFragment extends Fragment {

		private ListView lvItems;
		private View rootView;
		private Button btnAddItem;

		public MainFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_todo, container, false);

			lvItems = (ListView) rootView.findViewById(R.id.lvItems);
			lvItems.setAdapter(itemsAdapter);

			lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(R.string.remove_item_dialog_message)
							.setTitle(R.string.warning_dialog_title)
							.setPositiveButton(android.R.string.ok,	new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										final int index = lvItems.getFirstVisiblePosition();
										View itemView = lvItems.getChildAt(0);
										final int top = (itemView == null) ? 0 : itemView.getTop();
										
										removeTodoItem(position);
										
										lvItems.clearFocus();
										lvItems.post(new Runnable() {
						                    @Override
						                    public void run() {
						                    	lvItems.setSelectionFromTop(index, top);
						                    }
						                });

										dialog.dismiss();
									}
								})
							.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
							});

					AlertDialog dialog = builder.create();
					dialog.show();
					return true;
				}

			});

			lvItems.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(getActivity(), EditItemActivity.class);
					intent.putExtra(EditItemActivity.ITEM_DESCRIPTION, lvItems.getItemAtPosition(position).toString());
					intent.putExtra(EditItemActivity.ITEM_POSITION, position);

					getActivity().startActivityForResult(intent, TodoActivity.REQUEST_EDIT_ITEM);
				}
			});

			btnAddItem = (Button) rootView.findViewById(R.id.btnAddItem);
			btnAddItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addTodoItem();
				}
			});

			return rootView;
		}

		private void addTodoItem() {
			EditText etNewItem = (EditText) rootView.findViewById(R.id.etNewItem);
			itemsAdapter.add(etNewItem.getText().toString());
			etNewItem.setText("");
			((TodoActivity) getActivity()).writeItems();
		}

		private void removeTodoItem(int position) {
			itemsAdapter.remove(items.get(position));
			((TodoActivity) getActivity()).writeItems();
		}

	}

	public File getTodoFile() {
		File filesDir = getFilesDir();
		return new File(filesDir, "todo.txt");
	}

	public void readItems() {
		try {
			items = new ArrayList<String>(FileUtils.readLines(getTodoFile()));
		} catch (IOException e) {
			items = new ArrayList<String>();
		}
	}

	public void writeItems() {
		try {
			FileUtils.writeLines(getTodoFile(), items);
		} catch (IOException e) {
		}
	}

}
