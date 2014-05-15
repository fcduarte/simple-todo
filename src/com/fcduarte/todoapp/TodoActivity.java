package com.fcduarte.todoapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.Fragment;
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
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
        		items);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
    		int itemPosition = data.getIntExtra(EditItemActivity.ITEM_POSITION, -1);
    		
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
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					items.remove(position);
					itemsAdapter.notifyDataSetChanged();
					((TodoActivity) getActivity()).writeItems();
					return false;
				}
			});
            
            lvItems.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
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

        public void addTodoItem() {
        	EditText etNewItem = (EditText) rootView.findViewById(R.id.etNewItem);
        	itemsAdapter.add(etNewItem.getText().toString());
        	etNewItem.setText("");
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
