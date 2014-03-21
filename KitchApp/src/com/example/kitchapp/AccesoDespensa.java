package com.example.kitchapp;


import java.util.ArrayList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class AccesoDespensa extends Activity implements OnClickListener {
	

<<<<<<< HEAD
	Button b1;
=======
	
>>>>>>> Rama-Edu-Android
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acceso_despensa);

<<<<<<< HEAD
		b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(this);

		Button button_add=(Button)findViewById(R.id.button_add);
		button_add.setOnClickListener(this);
<<<<<<< HEAD
=======

>>>>>>> cbefd14e7b9a60ed8c3553c1b066bc11efc60619
=======
		Button button_dairy = (Button) findViewById(R.id.buttonDairy);
		button_dairy.setOnClickListener(this);
		Button button_fruits = (Button) findViewById(R.id.buttonFruits);
		button_fruits.setOnClickListener(this);
		Button button_bread = (Button) findViewById(R.id.buttonBread);
		button_bread.setOnClickListener(this);
		Button button_drinks = (Button) findViewById(R.id.buttonDrinks);
		button_drinks.setOnClickListener(this);
		Button button_meat = (Button) findViewById(R.id.buttonMeat);
		button_meat.setOnClickListener(this);
		Button button_fish = (Button) findViewById(R.id.buttonFish);
		button_fish.setOnClickListener(this);
		Button button_sauces = (Button) findViewById(R.id.buttonSauces);
		button_sauces.setOnClickListener(this);
		Button button_pasta = (Button) findViewById(R.id.buttonPasta);
		button_pasta.setOnClickListener(this);
		Button button_frozen = (Button) findViewById(R.id.buttonFrozen);
		button_frozen.setOnClickListener(this);
		Button button_others = (Button) findViewById(R.id.buttonOthers);
		button_others.setOnClickListener(this);

>>>>>>> Rama-Edu-Android
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.acceso_despensa, menu);
		return true;
	}
	

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){

		case R.id.buttonDairy:
			Intent i = new Intent(this,MostrarProductosCategoria.class);
<<<<<<< HEAD
			i.putExtra("lacteos", 1);
=======
			i.putExtra("idCat",1);
>>>>>>> Rama-Edu-Android
			startActivity(i);
			break;
		
		case R.id.buttonFruits:
			Intent j = new Intent(this,MostrarProductosCategoria.class);
			j.putExtra("idCat",2);
			startActivity(j);
			break;
			
		case R.id.buttonBread:
			Intent intent = new Intent(this,MostrarProductosCategoria.class);
			intent.putExtra("idCat",3);
			startActivity(intent);
			break;
			
		case R.id.buttonDrinks:
			Intent intent1 = new Intent(this,MostrarProductosCategoria.class);
			intent1.putExtra("idCat",4);
			startActivity(intent1);
			break;
			
		case R.id.buttonMeat:
			Intent intent2 = new Intent(this,MostrarProductosCategoria.class);
			intent2.putExtra("idCat",5);
			startActivity(intent2);
			break;
			
		case R.id.buttonFish:
			Intent intent3 = new Intent(this,MostrarProductosCategoria.class);
			intent3.putExtra("idCat",6);
			startActivity(intent3);
			break;
			
		case R.id.buttonSauces:
			Intent intent4 = new Intent(this,MostrarProductosCategoria.class);
			intent4.putExtra("idCat",7);
			startActivity(intent4);
			break;
			
		case R.id.buttonPasta:
			Intent intent5 = new Intent(this,MostrarProductosCategoria.class);
			intent5.putExtra("idCat",8);
			startActivity(intent5);
			break;
			
		case R.id.buttonFrozen:
			Intent intent6 = new Intent(this,MostrarProductosCategoria.class);
			intent6.putExtra("idCat",9);
			startActivity(intent6);
			break;
			
		case R.id.buttonOthers:
			Intent intent7 = new Intent(this,MostrarProductosCategoria.class);
			intent7.putExtra("idCat",10);
			startActivity(intent7);
			break;
			
		}
	}
<<<<<<< HEAD
	
	
<<<<<<< HEAD
	public void alertDialog(View v){
		final String [] items = new String[] {"Manualmente", "Voz", "C�digo de barras" };
	    final Integer[] icons = new Integer[] {R.drawable.teclado_android, R.drawable.microfono, R.drawable.barras};
	    ListAdapter adapter = new ItemAdapter(this, items, icons);
	    
        new AlertDialog.Builder(this).setAdapter(adapter, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int item ) {
        		if (item==0)
        			addManualmente();
        		else if (item == 1) {
        			addVoice();
        		}
        			
	        }
	    }).show();
	}
	
	
	public void addManualmente() {
		  Intent intent = new Intent(this,AddManualmente.class);
		  startActivity(intent);
	  }
=======
>>>>>>> cbefd14e7b9a60ed8c3553c1b066bc11efc60619
	
	public void addVoice() {
		if(isConnected()){
       	 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        	 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        	 RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        	 startActivityForResult(intent, REQUEST_CODE);
       			 }
       	else{
       		Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
       	}
	}
	
<<<<<<< HEAD
	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo net = cm.getActiveNetworkInfo();
	    if (net!=null && net.isAvailable() && net.isConnected()) {
	        return true;
	    } else {
	        return false; 
	    }
=======
	public void alertDialog(View v){
		final String [] items = new String[] {"Manualmente", "Voz", "C�digo de barras" };
	    final Integer[] icons = new Integer[] {R.drawable.teclado_android, R.drawable.microfono, R.drawable.barras};
	    ListAdapter adapter = new ItemAdapter(this, items, icons);
	    
        new AlertDialog.Builder(this).setAdapter(adapter, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int item ) {
        		if (item==0)
        			addManualmente();
	        }
	    }).show();
>>>>>>> cbefd14e7b9a60ed8c3553c1b066bc11efc60619
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
    	    
     match_text_dialog = new Dialog(AccesoDespensa.this);
     match_text_dialog.setContentView(R.layout.dialog_matches);
     match_text_dialog.setTitle("Select Matching Text");
     textlist = (ListView)match_text_dialog.findViewById(R.id.listDialogVoice);
     matches_text = data
		     .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
     ArrayAdapter<String> adapter =    new ArrayAdapter<String>(this,
    	     android.R.layout.simple_list_item_1, matches_text);
     textlist.setAdapter(adapter);
     textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     @Override
     public void onItemClick(AdapterView<?> parent, View view,
                             int position, long id) {
    	 
    	 match_text_dialog.hide();
    	 addProduct(position);
     }
 });
     
     match_text_dialog.show();
     
     }
     super.onActivityResult(requestCode, resultCode, data);
    }
=======
>>>>>>> Rama-Edu-Android
	
}
