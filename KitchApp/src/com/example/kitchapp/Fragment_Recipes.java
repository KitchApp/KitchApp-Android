package com.example.kitchapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;




import java.util.List;

import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_Recipes extends Fragment implements Interface{
	
	//private ArrayList<ItemReceta> options=new ArrayList<ItemReceta>();
	private ArrayList<ItemRecipeWithImage> options=new ArrayList<ItemRecipeWithImage>();
	private ListView list;
	private View rootView;
	private EditText userInput;
	boolean existRecipe=false;
	private Context context;
	private String searchFilter;	
	private Handler_Sqlite helper;
	private ArrayList<ItemProducto> products;
	public HashMap<String,ArrayList<String>> hashIngredients;
	public HashMap<String,String> hashImages;
	public HashMap<String,ArrayList<String>> hashQuantUnit;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		hashIngredients=new HashMap<String, ArrayList<String>>();
		hashImages=new HashMap<String, String>();
		hashQuantUnit=new HashMap<String, ArrayList<String>>();
		context = container.getContext();
		rootView = inflater.inflate(R.layout.activity_mostrar_categoria_recetas, container, false);	
		initializeArrayListRecipes();
		helper=new Handler_Sqlite(getActivity());
		list = (ListView)rootView.findViewById(R.id.listViewCatRec);
		ItemAdapterRecipeWithImage adapter;
		// Inicializamos el adapter.
		adapter = new ItemAdapterRecipeWithImage(getActivity(),options);
		// Asignamos el Adapter al ListView, en este punto hacemos que el
		// ListView muestre los datos que queremos.
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){	 
		    @Override
		    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		        // TODO Auto-generated method stub

		    	switch(position){
	    		
		    	// Recetas por Ingrediente
		        case 0: open_Dialog(position);
		        		break;

		        // Recetas por Nacionalidad		        		
		        case 1: open_Dialog(position);
		        		break;
		        
		        		// Recetas por Dieta	    	
		        case 2: open_Dialog_Spinner(position);
		        		break;
		         
		    	// Recetas por Recomendacion de mi Despensa    	
	        	case 3: showRecipesWithMyPantry();		   
	        		break;
	        		
	        		//Recetas r�pidas
		        case 4: new GetTitleImageByCookingMode().execute();
		        		break;
		        		
		        		// Recetas por Intolerancias
		        case 5: open_Dialog_Spinner(position);
        				break;
        		
        				// Recetas por Tipo de Plato
		        case 6: open_Dialog_Spinner(position);
						break;        				
		    	}
		    }
		});
		
		return rootView;	
	}	
	
	public void showRecipesWithMyPantry(){
		helper.open();
		
		
			products=helper.readAllProductsPantry();		
			//for(int i=0;i<products.size();i++){
				//GetIDRecipesPlusIngredientsByOneIngredient thread=new GetIDRecipesPlusIngredientsByOneIngredient(products.get(i));
			    GetIDRecipesPlusIngredientsByOneIngredient thread=new GetIDRecipesPlusIngredientsByOneIngredient();
				thread.delegate=this;
				thread.execute();			
			
		//}
		if(products.size()==0){
			Toast.makeText(getActivity(), "No tiene productos en su despensa", Toast.LENGTH_LONG).show();
		}
	}
	
	public ArrayList<String> processFinish(ArrayList<String> output){
		//En output se tiene la salida del onpostexecute de GetIDRecipesPlusIngredientsByOneIngredient
		//El m�todo devuelve un arraylist con un string con los titulos de las recetas, otro string con los ids de las recetas
		//y otro string con las urls de las imagenes -->este resultado lo recoge el m�todo onPostExecuto del hilo
		String keys="";
		String urlImages="";
		String titlesTmp="";
		ArrayList<String> resp=new ArrayList<String>();
		ArrayList<String> val=new ArrayList<String>();
		parsearIDRecipeAndIngredients(output.get(0));
		parsearIDRecipeAndImage(output.get(1));
		//parsearQuantAndUnit(output.get(2));
	
		int cont;
		Iterator<String> iter = hashIngredients.keySet().iterator();
		while(iter.hasNext()) {
			cont=0;
			String key = (String)iter.next();
		    val = hashIngredients.get(key);
		    String aux="";
		    String textoDesescapado;
		    for(int j=1;j<val.size();j++){
		    	     UnicodeEscaper escaper = UnicodeEscaper.above(127);
		    	     UnicodeUnescaper unescaper     = new UnicodeUnescaper();
		    	  
		    	     //String textoProblematico = "M�sica";
		    	  
		    	     //String textoEscapado = escaper.translate(textoProblematico);
		    	     // textoEscapado == "M\\u00FAsica", que se imprime como "M\u00FAsica"
		    	  
		    	     textoDesescapado = unescaper.translate(val.get(j));
		    	  
		    	      //cambiar manifest
		    	    if(helper.existProductAdded(textoDesescapado)){
		    	    	//if(helper.getUnitsProduct(textoDesescapado).equals(object)){
		    	    		
		    	    	//}
						cont++;
					}
		    		 
		    }
		    if(cont==val.size()-1){
		    	keys=keys+key+",";
		    	urlImages=urlImages+hashImages.get(key)+",";
				titlesTmp=titlesTmp+val.get(0)+",";
					// TODO Auto-generated catch block
		    }
		}
		resp.add(titlesTmp);
		resp.add(keys);
		resp.add(urlImages);
		return resp;
	}	
	
	
	public void parsearIDRecipeAndImage(String result){
		String []tmp1=result.split("\"");
		for(int j=1;j<tmp1.length;j=j+2){
			hashImages.put(tmp1[j], tmp1[j+1]);
		}
	}
	
	public void parsearIDRecipeAndIngredients(String result){
		String []tmp1=result.split("\""+"\\["+"\"");
		String []value1=tmp1[1].split("\""+"\\]"+"\\["+"\"");
		String []value2;
		String []key;
		ArrayList<String> valuetmp=new ArrayList<String>();
		key=tmp1[0].split("\"");
		String myKey=key[1];
		String[] tmp2=value1[value1.length-1].split("\""+"\\]"+"\"");
		String[] myValue=value1;
		myValue[myValue.length-1]=tmp2[0];
		myValue[myValue.length-1]=myValue[myValue.length-1].split("\""+"\\]")[0];
		for(int k=0;k<myValue.length;k++){
			valuetmp.add(myValue[k]);
		}
		
		hashIngredients.put(myKey, valuetmp);
		for(int i=1;i<tmp1.length-1;i++){
			valuetmp=new ArrayList<String>();
			key=tmp1[i].split("\""+"\\]"+"\"");
			value2=tmp1[i+1].split("\""+"\\]"+"\\["+"\"");
			value2[value2.length-1]=value2[value2.length-1].split("\""+"\\]")[0];
			for(int j=0;j<value2.length;j++){
					valuetmp.add(value2[j]);
					
			}
			hashIngredients.put(key[1], valuetmp);
		}
	}
	
	private void initializeArrayListRecipes() {
		options.add(new ItemRecipeWithImage(R.drawable.ingrediente,"Por ingrediente"));
		options.add(new ItemRecipeWithImage(R.drawable.nacionalidad,"Nacionalidad"));
		options.add(new ItemRecipeWithImage(R.drawable.dieta,"Dieta"));
		options.add(new ItemRecipeWithImage(R.drawable.midespensa,"Con mi despensa"));
		options.add(new ItemRecipeWithImage(R.drawable.rapidas,"Modo de cocina(Rapidas)"));
		options.add(new ItemRecipeWithImage(R.drawable.intolerancias,"Intolerancias"));
		options.add(new ItemRecipeWithImage(R.drawable.tipoplato,"Tipo de plato"));		
	}
	
	
	public void open_Dialog(final int position) {

		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_name_ingredient_recipe, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
		switch (position) {
			case 0: userInput.setHint("Introduzca ingrediente");
					break;
			case 1: userInput.setHint("Introduzca nacionalidad");
					break;
			case 3: userInput.setHint("Introduzca ");
					break;			
		}
        
		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
					    	switch (position){
					    	case 0: new GetTitleImageByIngredient().execute();			    
					    			break;
					    			
					    	case 1: new GetTitleImageByNationality().execute();
					    			break;
					    	}
					    	
					    	//resto de opciones
					    	
					    }
				})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    }
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	

	
	public void open_Dialog_Spinner(final int position){
		
		String aux= "";		
		switch (position) {
			case 2: aux= "Seleccione una Dieta";
				break;
			case 5: aux= "Seleccione Intolerancia";
				break;	
			case 6: aux= "Seleccione tipo de plato";
				break;
				
		}
		
		AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity());
	    Dialog.setTitle(aux);

	    LayoutInflater li = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View dialogView = li.inflate(R.layout.dialog_recipe_spinner, null);
	    Dialog.setView(dialogView);

	    Dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int whichButton) {
	                	   switch (position){
	                	   		case 2: new GetTitleImageByDiet().execute();
	                	   			break;
	                	   		case 5: new GetTitleImageIntolerances().execute();
                	   				break;
	                	   		case 6: new GetTitleImageByTypeOfDish().execute();
             	   					break;	
	                	   		  }		
	                   }
	    });	    	    

	    Dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int whichButton) {

	                   }
	    });
	     
	    Dialog.show();
	     
	    Spinner spinnercategory = (Spinner)dialogView.findViewById(R.id.viewSpin);
	    
	    switch (position){
	    
	    case 2: ArrayAdapter<CharSequence> adapterDiet = ArrayAdapter.createFromResource(context, R.array.dietas, android.R.layout.simple_spinner_item);	     
	    		adapterDiet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    		spinnercategory.setAdapter(adapterDiet);
	    		break;
	    
	    case 5: ArrayAdapter<CharSequence> adapterIntolerance = ArrayAdapter.createFromResource(context, R.array.intolerancias, android.R.layout.simple_spinner_item);	     
	     		adapterIntolerance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     		spinnercategory.setAdapter(adapterIntolerance);
				break;
	    
	    case 6: ArrayAdapter<CharSequence> adapterTipoPlato = ArrayAdapter.createFromResource(context, R.array.tipoPlato, android.R.layout.simple_spinner_item);	     
	    		adapterTipoPlato.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    		spinnercategory.setAdapter(adapterTipoPlato);
	    		break;
	    }
	    	    
	     spinnercategory.setOnItemSelectedListener(new OnItemSelectedListener() {

	     public void onItemSelected(AdapterView<?> parent, View arg1,int arg2, long arg3) {
	    	 searchFilter = parent.getSelectedItem().toString();
	    	 //String tmp=searchFilter;	    	 
	     }

	     public void onNothingSelected(AdapterView<?> arg0) {
	                // TODO Auto-generated method stub
	     }
	     });
		
	}	
	
	// (0) Busqueda por Ingrediente.
	private class GetTitleImageByIngredient extends AsyncTask<String, Integer, ArrayList<String>>{
		
		ArrayList<String> resp=new ArrayList<String>();
		HttpPost httppost1;
		HttpPost httppost2;
		
		@Override
	    protected ArrayList<String> doInBackground(String... urls) {
	    	
			HttpClient httpclient = new DefaultHttpClient();
			String searchFilter=userInput.getText().toString().trim();
			//set the remote endpoint URL
		    
			try {
				httppost1 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByIngredient.php?field_ingrediente_nombre_value="+URLEncoder.encode(searchFilter,"UTF-8"));
				httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByIngredient.php?field_ingrediente_nombre_value="+URLEncoder.encode(searchFilter,"UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    		    
		    try {
		
		        JSONObject json1 = new JSONObject();
		        JSONObject json2 = new JSONObject();
		        //add serialised JSON object into POST request
		        StringEntity se1 = new StringEntity(json1.toString());
		        StringEntity se2 = new StringEntity(json2.toString());
		        //set request content type
		        se1.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		        httppost1.setEntity(se1);
		        se2.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		        httppost2.setEntity(se2);
				
		        //send the POST request
		        HttpResponse response1 = httpclient.execute(httppost1);
		        HttpResponse response2 = httpclient.execute(httppost2);
		
		        //read the response from Services endpoint
		        String jsonResponse1 = EntityUtils.toString(response1.getEntity());
		        String jsonResponse2 = EntityUtils.toString(response2.getEntity());
		        //if (!jsonResponse1.equals("")){
		        	//existRecipe=true;
		        	resp.add(jsonResponse1);
		        	resp.add(jsonResponse2);
		        	resp.add(searchFilter);
		        //}
		        
		        return resp;		        
		
		    }catch (Exception e) {
		        Log.v("Error adding article", e.getMessage());
		    }
		
		    return null;
		}
	
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			//Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
		    //apellido.setText(result);
			//if(existRecipe){
				Intent intent = new Intent(getActivity(),ShowListRecipes.class);
				intent.putStringArrayListExtra("recipes", result);
		    	startActivity(intent);
			//}	    		    
		}
	}
			
	// (1) Busqueda por nacionalidad
	private class GetTitleImageByNationality extends AsyncTask<String, Integer, ArrayList<String>>{
	
		ArrayList<String> resp=new ArrayList<String>();
		HttpPost httppost3;
		HttpPost httppost4;
		@Override
		protected ArrayList<String> doInBackground(String... urls) {
		    	
			HttpClient httpclient = new DefaultHttpClient();
			String searchFilter=userInput.getText().toString().trim();
		    //set the remote endpoint URL
			if (searchFilter.equals("Espa�ola") || searchFilter.equals("espa�ola"))
				searchFilter="Espanola";
			    
			try{
				httppost3 = new HttpPost("http://www.kitchapp.es/getTitleByNationality.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
				httppost4 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByNationality.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));	
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
		    try {
			
		        JSONObject json3 = new JSONObject();
		        JSONObject json4 = new JSONObject();
		        //add serialised JSON object into POST request
		        StringEntity se3 = new StringEntity(json3.toString());
		        StringEntity se4 = new StringEntity(json4.toString());
		        //set request content type
		        se3.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		        httppost3.setEntity(se3);
		        se4.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		        httppost4.setEntity(se4);
					
		        //send the POST request
		        HttpResponse response3 = httpclient.execute(httppost3);
		        HttpResponse response4 = httpclient.execute(httppost4);
			
		        //read the response from Services endpoint
		        String jsonResponse3 = EntityUtils.toString(response3.getEntity());
		        String jsonResponse4 = EntityUtils.toString(response4.getEntity());
		        //if (!jsonResponse3.equals("")){
		        	//existRecipe=true;
		        	resp.add(jsonResponse3);
		        	resp.add(jsonResponse4);
		        	resp.add(searchFilter);
		        //}
		        return resp;		        
			
		    }catch (Exception e) {
		        Log.v("Error adding article", e.getMessage());
		    }
			
		    return null;
		}
		
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			//Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
		    //apellido.setText(result);
			//if(existRecipe){
				Intent intent = new Intent(getActivity(),ShowListRecipes.class);				
				intent.putStringArrayListExtra("recipes", result);
		    	startActivity(intent);
			//}				
		}
	}
	
	
	// (2) Busqueda por Dieta
		private class GetTitleImageByDiet extends AsyncTask<String, Integer, ArrayList<String>>{
			
			ArrayList<String> resp=new ArrayList<String>();
			HttpPost httppost1;
			HttpPost httppost2;
			
			@Override
		    protected ArrayList<String> doInBackground(String... urls) {
		    	
				HttpClient httpclient = new DefaultHttpClient();
				//String searchFilter=userInput.getText().toString().trim();
			    //set the remote endpoint URL
			    //HttpPost httppost1 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByDiet.php?field_dieta_value="+searchFilter);
				//HttpPost httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByDiet.php?field_dieta_value="+searchFilter);
				try {
					httppost1 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByDiet.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
					httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByDiet.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    		    
			    try {
			
			        JSONObject json1 = new JSONObject();
			        JSONObject json2 = new JSONObject();
			        //add serialised JSON object into POST request
			        StringEntity se1 = new StringEntity(json1.toString());
			        StringEntity se2 = new StringEntity(json2.toString());
			        //set request content type
			        se1.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			        httppost1.setEntity(se1);
			        se2.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			        httppost2.setEntity(se2);
					
			        //send the POST request
			        HttpResponse response1 = httpclient.execute(httppost1);
			        HttpResponse response2 = httpclient.execute(httppost2);
			
			        //read the response from Services endpoint
			        String jsonResponse1 = EntityUtils.toString(response1.getEntity());
			        String jsonResponse2 = EntityUtils.toString(response2.getEntity());
			        //if (!jsonResponse1.equals("")){
			        	//existRecipe=true;
			        	resp.add(jsonResponse1);
			        	resp.add(jsonResponse2);
			        	resp.add(searchFilter);
			       // }
			       
			        return resp;
			        		
			    }catch (Exception e) {
			        Log.v("Error adding article", e.getMessage());
			    }
			
			    return null;
			}
		
			// onPostExecute displays the results of the AsyncTask.
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				//Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			    //apellido.setText(result);
				//if(existRecipe){
					Intent intent = new Intent(getActivity(),ShowListRecipes.class);
					intent.putStringArrayListExtra("recipes", result);
			    	startActivity(intent);
				//}	    		    
			}
		}
	
		
		//con mi despensa
		private class GetIDRecipesPlusIngredientsByOneIngredient extends AsyncTask<String, Integer, ArrayList<String>>{
			public Interface delegate=null;
			ArrayList<String> resp=new ArrayList<String>();
			HttpPost httppost1;
			HttpPost httppost2;
			HttpPost httppost3;
			ItemProducto prod=null;
			//String searchFilter;
			
			/*public GetIDRecipesPlusIngredientsByOneIngredient(ItemProducto itemProducto) {
				// TODO Auto-generated constructor stub
				if(itemProducto!=null){
					prod=itemProducto;
				}
			}*/
			@Override
		    protected ArrayList<String> doInBackground(String... urls) {
		    	
				HttpClient httpclient = new DefaultHttpClient();
				/*if (prod==null)
					searchFilter=userInput.getText().toString().trim();
				else
					searchFilter=prod.getNombre();*/
				//set the remote endpoint URL
				httppost1 = new HttpPost("http://www.kitchapp.es/getIDRecipesPlusIngredientsByOneIngredient.php?");
				httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesPlusIDImagesByOneIngredient.php?");
				httppost3 = new HttpPost("http://www.kitchapp.es/getQuantityPlusUnitsByOneIngredient.php?");
				/*try {
					httppost1 = new HttpPost("http://www.kitchapp.es/getIDRecipesPlusIngredientsByOneIngredient.php?field_ingrediente_nombre_value="+URLEncoder.encode(searchFilter,"UTF-8"));
					httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesPlusIDImagesByOneIngredient.php?field_ingrediente_nombre_value="+URLEncoder.encode(searchFilter,"UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
			    try {
			        JSONObject json1 = new JSONObject();
			        JSONObject json2 = new JSONObject();
			        JSONObject json3 = new JSONObject();
			        
			        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			        String size=Integer.toString(products.size());
			        pairs.add(new BasicNameValuePair("size", size));
			        for(int i=0;i<products.size();i++){
			        	pairs.add(new BasicNameValuePair(Integer.toString(i), products.get(i).getNombre()));
			        }
			        
			        
			        httppost1.setEntity(new UrlEncodedFormEntity(pairs));
			        
			        //add serialised JSON object into POST request
			       /* StringEntity se1 = new StringEntity(json1.toString());
			        StringEntity se2 = new StringEntity(json2.toString());*/
			        //set request content type
			        /*se1.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			        httppost1.setEntity(se1);*/
			        //se2.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			        httppost2.setEntity(new UrlEncodedFormEntity(pairs));
			        httppost3.setEntity(new UrlEncodedFormEntity(pairs));
			        //send the POST request
			        HttpResponse response1 = httpclient.execute(httppost1);
			        HttpResponse response2 = httpclient.execute(httppost2);
			        HttpResponse response3 = httpclient.execute(httppost3);
			        //read the response from Services endpoint
			        String jsonResponse1 = EntityUtils.toString(response1.getEntity());
			        String jsonResponse2 = EntityUtils.toString(response2.getEntity());
			        String jsonResponse3 = EntityUtils.toString(response3.getEntity());
			        //jsonResponse2 = new String(jsonResponse2.getBytes("UTF-8"), "UTF-8");
			        if (!jsonResponse1.equals("")){
			        	//existRecipe=true;
			        	resp.add(jsonResponse1);
			        	resp.add(jsonResponse2);
			        	resp.add(jsonResponse3);
			        	//resp.add(searchFilter);
			        }
			        
			        return resp;
			    }catch (Exception e) {
			        Log.v("Error adding article", e.getMessage());
			    }
			    return null;
			}
		
			// onPostExecute displays the results of the AsyncTask.
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				String titlesTmp="";
				String ids="";
				String urlImage="";
				ArrayList<String> resp=new ArrayList<String>();
				resp=delegate.processFinish(result);
				UnicodeUnescaper unescaper1     = new UnicodeUnescaper();  
				titlesTmp=unescaper1.translate(resp.get(0));
				ids=resp.get(1);
				urlImage=resp.get(2);
				ArrayList<String> bundle=new ArrayList<String>();
				bundle.add(titlesTmp);
				bundle.add(ids);
				bundle.add(urlImage);
				Intent intent = new Intent(getActivity(),ShowListRecipes.class);
				intent.putStringArrayListExtra("pantry", bundle); 
			    startActivity(intent);
			}
			
		}
		
		// (4) Por modo de cocina: Rapidas
					private class GetTitleImageByCookingMode extends AsyncTask<String, Integer, ArrayList<String>>{
							ArrayList<String> resp=new ArrayList<String>();
							HttpPost httppost5;
							HttpPost httppost6;
							@Override
						    protected ArrayList<String> doInBackground(String... urls) {
						    	
								HttpClient httpclient = new DefaultHttpClient();
								//String searchFilter=userInput.getText().toString().trim();
							    //set the remote endpoint URL
								/*try{
									httppost5 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByCookingMode.php?field_tiempo_de_preparacion_value="+URLEncoder.encode(searchFilter,"UTF-8"));
									httppost6 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByCookingMode.php?field_tiempo_de_preparacion_value="+URLEncoder.encode(searchFilter,"UTF-8"));
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}*/
								httppost5 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByCookingMode.php?");
								httppost6 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByCookingMode.php?");
							    try {					
							        JSONObject json5 = new JSONObject();
							        JSONObject json6 = new JSONObject();
							        //add serialised JSON object into POST request
							        StringEntity se5 = new StringEntity(json5.toString());
							        StringEntity se6 = new StringEntity(json6.toString());
							        //set request content type
							        se5.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
							        httppost5.setEntity(se5);
							        se6.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
							        httppost6.setEntity(se6);
							        
								    //send the POST request
								    HttpResponse response5 = httpclient.execute(httppost5);
								    HttpResponse response6 = httpclient.execute(httppost6);
								
							        //read the response from Services endpoint
							        String jsonResponse5 = EntityUtils.toString(response5.getEntity());
							        String jsonResponse6 = EntityUtils.toString(response6.getEntity());
							        //if (!jsonResponse5.equals("")){
							        	//existRecipe=true;
							        	resp.add(jsonResponse5);
							        	resp.add(jsonResponse6);
							        	resp.add(searchFilter);
							        //}
							        return resp;		        
							
							    }catch (Exception e) {
							        Log.v("Error adding article", e.getMessage());
							    }
								
							    return null;
							}
						
							// onPostExecute displays the results of the AsyncTask.
							@Override
							protected void onPostExecute(ArrayList<String> result) {
								//Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
							    //apellido.setText(result);
								//if(existRecipe){
									Intent intent = new Intent(getActivity(),ShowListRecipes.class);						
									intent.putStringArrayListExtra("recipes", result);
							    	startActivity(intent);
								//}											    		    
							}
					}					
		
					
					// (4) Por intolerancia
					private class GetTitleImageIntolerances extends AsyncTask<String, Integer, ArrayList<String>>{
						ArrayList<String> resp=new ArrayList<String>();
						HttpPost httppost7;
						HttpPost httppost8;
								
						@Override
					    protected ArrayList<String> doInBackground(String... urls) {
										    	
							HttpClient httpclient = new DefaultHttpClient();
							try {									
								httppost7 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByIntolerances.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
								httppost8 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByIntolerances.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
									
							try {
											
								JSONObject json7 = new JSONObject();
								JSONObject json8 = new JSONObject();
						        //add serialised JSON object into POST request
						        StringEntity se7 = new StringEntity(json7.toString());
								StringEntity se8 = new StringEntity(json8.toString());
								//set request content type
								se7.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
								httppost7.setEntity(se7);
								se8.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
								httppost8.setEntity(se8);
								    
								//send the POST request
					 			HttpResponse response7 = httpclient.execute(httppost7);
					 			HttpResponse response8 = httpclient.execute(httppost8);
										
					            //read the response from Services endpoint
								String jsonResponse7 = EntityUtils.toString(response7.getEntity());
								String jsonResponse8 = EntityUtils.toString(response8.getEntity());
								//if (!jsonResponse7.equals("")){
								   	//  existRecipe=true;
								    resp.add(jsonResponse7);
								    resp.add(jsonResponse8);
								    resp.add(searchFilter);
						        //}
					            return resp;		        
												
							}catch (Exception e) {
							      Log.v("Error adding article", e.getMessage());
							}								
						    return null;
						}
											
						// onPostExecute displays the results of the AsyncTask.
						@Override			
						protected void onPostExecute(ArrayList<String> result) {				
							//if(existRecipe){
								Intent intent = new Intent(getActivity(),ShowListRecipes.class);
								intent.putStringArrayListExtra("recipes", result);
						    	startActivity(intent);
							//}										    		    
						}
					}
				
					// (6) Busqueda por Tipo de Plato
					private class GetTitleImageByTypeOfDish extends AsyncTask<String, Integer, ArrayList<String>>{
								
						ArrayList<String> resp=new ArrayList<String>();
						HttpPost httppost1;
						HttpPost httppost2;
								
						@Override
					    protected ArrayList<String> doInBackground(String... urls) {
						    	
							HttpClient httpclient = new DefaultHttpClient();
							//String searchFilter=userInput.getText().toString().trim();
						    //set the remote endpoint URL
						    //HttpPost httppost1 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByDiet.php?field_dieta_value="+searchFilter);
							//HttpPost httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByDiet.php?field_dieta_value="+searchFilter);
							try {
								httppost1 = new HttpPost("http://www.kitchapp.es/getRecipesTitleByTypeOfDish.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
								httppost2 = new HttpPost("http://www.kitchapp.es/getUrlsRecipesImagesByTypeOfDish.php?name="+URLEncoder.encode(searchFilter,"UTF-8"));
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
								    		    
						    try {
							
						        JSONObject json1 = new JSONObject();
						        JSONObject json2 = new JSONObject();
						        //add serialised JSON object into POST request
						        StringEntity se1 = new StringEntity(json1.toString());
						        StringEntity se2 = new StringEntity(json2.toString());
						        //set request content type
						        se1.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
						        httppost1.setEntity(se1);
						        se2.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
						        httppost2.setEntity(se2);
									
						        //send the POST request
						        HttpResponse response1 = httpclient.execute(httppost1);
						        HttpResponse response2 = httpclient.execute(httppost2);
							
						        //read the response from Services endpoint
								String jsonResponse1 = EntityUtils.toString(response1.getEntity());
						        String jsonResponse2 = EntityUtils.toString(response2.getEntity());
						        //if (!jsonResponse1.equals("")){
						        	//existRecipe=true;
						        	resp.add(jsonResponse1);
						        	resp.add(jsonResponse2);
						        	resp.add(searchFilter);
						        // }
							       
					           return resp;
								        		
						    }catch (Exception e) {
								        Log.v("Error adding article", e.getMessage());
							}
								
							return null;
						}
							
						// onPostExecute displays the results of the AsyncTask.
						@Override
						protected void onPostExecute(ArrayList<String> result) {
							
							//if(existRecipe){
								Intent intent = new Intent(getActivity(),ShowListRecipes.class);
								intent.putStringArrayListExtra("recipes", result);
						    	startActivity(intent);
							//}	    		    
						}
					}
		
}
