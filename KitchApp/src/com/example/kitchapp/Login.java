package com.example.kitchapp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
=======

>>>>>>> Rama-Vivi-Android
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {
	
	private EditText userName;
	private EditText password;
	Handler_Sqlite helper = new Handler_Sqlite(this);
	Boolean registrado=false;

	public String session_name;
<<<<<<< HEAD
	public String session_id;
    	public String encryptedPassword="";
=======
    public String session_id;
    public String encryptedPassword="";
>>>>>>> Rama-Vivi-Android
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(PantallaTransicion.PREFS_NAME, 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        
        if (hasLoggedIn) {
        	Intent intent = new Intent(Login.this,PantallaTransicion.class);
	        startActivity(intent);
			finish();
        }
        
     // setting default screen to login.xml
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
     
        setContentView(R.layout.activity_login);

		
		userName = (EditText)findViewById(R.id.editTextuserName);
		password = (EditText)findViewById(R.id.editTextPassword);

		TextView registerScreen = (TextView)findViewById(R.id.link_to_register);
		Button b1=(Button)findViewById(R.id.btnLogin);
		
	}

		

	public void intento_logueo(View view) {
		String name = userName.getText().toString(); 
		String key = password.getText().toString();
		
		
		new HttpAsyncTask().execute();
	}
	
	public void registrarse(View view) {
		Intent i = new Intent(this, Registro.class);
		startActivity(i);
	}
	
	
	public void errorLogging() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 
		builder.setTitle("Error")
	            .setIcon(
	                    getResources().getDrawable(
	                            R.drawable.close))

	            .setMessage("No se encuentra registrado en KitchApp. Por favor registrese")

	            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
	 
	                @Override
	                public void onClick(DialogInterface arg0, int arg1) {
	                	arg0.cancel();
	                }
	            });
	 
	    	builder.create();
	    	builder.show();
	}
   

	private class HttpAsyncTask extends AsyncTask<String, Integer, Integer> {
		

<<<<<<< HEAD
            @Override
            protected Integer doInBackground(String... urls) {
        	     	
=======
		
        @Override
        protected Integer doInBackground(String... urls) {
        	
        	
>>>>>>> Rama-Vivi-Android
        	HttpClient httpclient = new DefaultHttpClient();

            	//set the remote endpoint URL
            	HttpPost httppost = new HttpPost("http://www.kitchapp.es/json/user/login");
            
            	try {

                	//get the UI elements for username and password
            		EditText username= (EditText) findViewById(R.id.editTextuserName);
                	EditText password= (EditText) findViewById(R.id.editTextPassword);

	                JSONObject json = new JSONObject();
        	        //extract the username and password from UI elements and create a JSON object
                	json.put("username", username.getText().toString().trim());
                	json.put("password", password.getText().toString().trim());

                	//add serialised JSON object into POST request
                	StringEntity se = new StringEntity(json.toString());
                	//set request content type
                	se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                	httppost.setEntity(se);

	                //send the POST request
        	        HttpResponse response = httpclient.execute(httppost);

                	//read the response from Services endpoint
                	String jsonResponse = EntityUtils.toString(response.getEntity());

                	JSONObject jsonObject = new JSONObject(jsonResponse);
                	//read the session information
                	session_name=jsonObject.getString("session_name");
                	session_id=jsonObject.getString("sessid");
                	if (jsonObject!=null)
	                	registrado=true;
        	        return 0;

            	}catch (Exception e) {
                	Log.v("Error adding article", e.getMessage());
            	}

            	return 0;
            }

<<<<<<< HEAD
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(Integer result) {
	      	//Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
	       	//apellido.setText(result);
	       	if(registrado){
	 		Intent intent = new Intent(Login.this,PantallaTransicion.class);
	     		startActivity(intent);
	 		finish();
	    			
	 		/*Toast.makeText(getBaseContext(), "Perfecto!", Toast.LENGTH_LONG).show();
	 		Intent i = new Intent(this, PantallaTransicion.class);
	 		startActivity(i);*/
	 	}
	 	else
	 		errorLogging();
	            
	        }
        
	    }

   	    public static String httpGetData(String mURL) {
            	InputStream inputStream = null;
=======
            return 0;
        }

       /** onPostExecute displays the results of the AsyncTask.**/
       @Override
        protected void onPostExecute(Integer result) {
            if(registrado){
        		SharedPreferences settings = getSharedPreferences(PantallaTransicion.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				
				editor.putBoolean("hasLoggedIn", true);
				editor.commit();

    			Intent intent = new Intent(Login.this,PantallaTransicion.class);
    	    	startActivity(intent);
    			finish();
    		}
    		else
    			errorLogging();
            
            
       }
    }


	
	public static String httpGetData(String mURL) {
        InputStream inputStream = null;
>>>>>>> Rama-Vivi-Android
	        String result = "";
	        try {
	 
	            // create HttpClient
	            HttpClient httpclient = new DefaultHttpClient();
	 
	            // make GET request to the given URL
	            HttpResponse httpResponse = httpclient.execute(new HttpGet(mURL));
	 
	            // receive response as inputStream
	            inputStream = httpResponse.getEntity().getContent();
	 
	            // convert inputstream to string
	            if(inputStream != null)
	                result = convertInputStreamToString(inputStream);
	            else
	                result = "Did not work!";
	 
	        } catch (Exception e) {
	            Log.d("InputStream", e.getLocalizedMessage());
	        }
	 
<<<<<<< HEAD
	        return result;    
            }	
	 
   	    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        	BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        	String line = "";
        	String result = "";
        	while((line = bufferedReader.readLine()) != null)
            		result += line;
 
	        inputStream.close();
	        return result;
    	    }
}
=======
	        return result;
    
    }  
	
	 
	 private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;
	 
	        inputStream.close();
	        return result;
	 
	    }
	
	 }
	
	
>>>>>>> Rama-Vivi-Android
