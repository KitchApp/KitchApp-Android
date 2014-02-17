package com.example.kitchapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class Registro extends Activity implements OnClickListener {
	
	private EditText userName;
	private EditText password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.registro);
 
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        Button buttonRegister = (Button) findViewById(R.id.btnRegistro);
        userName = (EditText) findViewById(R.id.reg_usuario);
        password = (EditText) findViewById(R.id.reg_password);
 
        // Listening to Login Screen link
        loginScreen.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
 
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			
			case R.id.link_to_login:
				finish();
				break;
			case R.id.btnRegistro:
				Intent intent = new Intent(this,Login.class);
				intent.putExtra("userName",userName.getText().toString());
				intent.putExtra("password",password.getText().toString());
				startActivity(intent);
				finish();
				break;
		}
		
	}
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/Rama-Vivi-Android
