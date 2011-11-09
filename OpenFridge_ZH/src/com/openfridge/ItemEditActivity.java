package com.openfridge;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//TODO Need to post updates correctly from item edit menu rather than always
// adding new ones ZH/EL
//TODO Change date picker to simpler version SC
//TODO Implement saved food behaviour        ZH/EL/JW
//
public class ItemEditActivity extends Activity {
    private EditText descField;
    private DatePicker datePicker;
    private static final int MAX_WIDTH_OFFSET = 10;
    private static final int USER_ID = 1;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FridgeFood prechosenFood 
            = FridgeFood.getFoodFromBundle(getIntent().getExtras());
        setContentView(R.layout.item_edit);

        descField = (EditText) findViewById(R.id.editText1);
        datePicker = (DatePicker) findViewById(R.id.datePicker1);
        // Prepopulate date and description field if this menu was accessed 
        // from the ExpireActivity
        if (prechosenFood != null) {
            String description = prechosenFood.getDescription();
            GregorianCalendar expirationDate 
                = prechosenFood.getExpirationDate();
            
            descField.setText(description);
            descField.setSelection(description.length());

            datePicker.updateDate(expirationDate.get(Calendar.YEAR), 
                    expirationDate.get(Calendar.MONTH) - 1, // Month from 0
                    expirationDate.get(Calendar.DAY_OF_MONTH));
        }
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Common_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Add the common items buttons which can be clicked to instantly
        // add a common item
        addCommonItemButtons(adapter);
    }
	
	private void addCommonItemButtons(ArrayAdapter<CharSequence> descriptions) {
	    LinearLayout layout 
            = (LinearLayout) findViewById(R.id.common_items_buttons);
	    Display display = getWindowManager().getDefaultDisplay();
	    int maxWidth = display.getWidth() - MAX_WIDTH_OFFSET;
	    
	    int currentWidth = 0;
	    LinearLayout currentLayout = getHorizontalLinearLayout();
	    for (int i = 0; i < descriptions.getCount(); i++) {
	        CharSequence desc = descriptions.getItem(i);
            
            Button itemButton = new Button(this);
            itemButton.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            itemButton.setText(desc.toString());
            itemButton.setOnClickListener(
                    new CommonFoodButtonOnClickListener());
            
            itemButton.measure(0, 0);
            int buttonWidth = itemButton.getMeasuredWidth();
            if (currentWidth + buttonWidth > maxWidth) {
                layout.addView(currentLayout);
                currentLayout = getHorizontalLinearLayout();
                currentWidth = 0;
            }
            
            currentWidth += buttonWidth;
            // If the button won't fit on one line, force it a single line
            if (buttonWidth > maxWidth && currentWidth == buttonWidth) {
                itemButton = new Button(this);
                itemButton.setLayoutParams(new LayoutParams(
                        maxWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                itemButton.setText(desc.toString());
                currentLayout.addView(itemButton);
                layout.addView(currentLayout);
                
                itemButton.setOnClickListener(
                        new CommonFoodButtonOnClickListener());
                currentLayout = getHorizontalLinearLayout();
                currentWidth = 0;
            } else {
                currentLayout.addView(itemButton);
            }
	    }
	    
	    layout.addView(currentLayout);
	}
	
	private LinearLayout getHorizontalLinearLayout() {
	    LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.HORIZONTAL);
        
        return ll;
	}
	
	private String getSimpleDateString() {
	    return datePicker.getYear() + "-" + datePicker.getMonth() + "-" + 
	           datePicker.getDayOfMonth();
	}
	
	public void doneEditClick(View view){
	    String description = descField.getText().toString();
	    if (!description.equals("")) {
	        try {
	            postNewFood(description.toString(), getSimpleDateString(), 
	                        USER_ID);
	        } catch (IOException e) {
	            Toast.makeText(getApplicationContext(), "Communication Error", 
	                    Toast.LENGTH_SHORT).show();
	        }
	        finish();
	    } else {
	        Toast.makeText(view.getContext(), "Please enter a food description", 
	                       Toast.LENGTH_SHORT).show();
	    }
	}
	
	private void postNewFood(String desc, String expirationDate, int userId)
	        throws IOException {
	    DataClient client = DataClient.getInstance();
	    FridgeFood food 
	        = new FridgeFood(desc, expirationDate, Integer.toString(userId));

	    client.postFood(food);

	}
	
	private class CommonFoodButtonOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                // Currently just add the food based on button name and
                // datepicker

                postNewFood(((Button) v).getText().toString(), 
                        getSimpleDateString(), USER_ID);
            } catch (IOException e) {
                Toast.makeText(v.getContext(), "Communication Error", 
                        Toast.LENGTH_SHORT).show();
            }
            
            finish();
        }
	}
}
