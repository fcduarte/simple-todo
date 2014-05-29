package com.fcduarte.todoapp;

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
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends Activity {
	
	public static final String ITEM_DESCRIPTION = "item_description";
	public static final String ITEM_POSITION = "item_position";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new EditItemFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item, menu);
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
	
	public static class EditItemFragment extends Fragment {

		private EditText etEditItem;
		private String itemDescription;
		private int itemPosition;
		private Button btnSaveItem;
		
		public EditItemFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_edit_item,
					container, false);

			Intent intent = getActivity().getIntent();
			itemDescription = intent.getStringExtra(EditItemActivity.ITEM_DESCRIPTION);
			itemPosition = intent.getIntExtra(EditItemActivity.ITEM_POSITION, -1);
			
			etEditItem = (EditText) rootView.findViewById(R.id.etEditItem);
			etEditItem.setText(itemDescription);
			etEditItem.setSelection(itemDescription.length());
			
			btnSaveItem = (Button) rootView.findViewById(R.id.btnSaveItem);
			btnSaveItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String newItemDescription = etEditItem.getText().toString();
					
					if (newItemDescription.equals(itemDescription)) {
						getActivity().setResult(TodoActivity.RESULT_NOT_MODIFIED);
					} else {
						Intent data = new Intent();
						data.putExtra(EditItemActivity.ITEM_DESCRIPTION, newItemDescription);
						data.putExtra(EditItemActivity.ITEM_POSITION, itemPosition);
						getActivity().setResult(TodoActivity.RESULT_OK, data);
					}
					
					getActivity().finish();
				}
			});
			
			return rootView;
		}
	}

}
