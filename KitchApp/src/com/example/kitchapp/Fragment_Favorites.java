package com.example.kitchapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_Favorites extends Fragment{
	View rootView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.activity_fragment_favorites, container, false);
		
		TextView texto = (TextView) rootView.findViewById(R.id.texto_favorites);
		
		texto.setText("Tab seleccionada" + "\n\n" + "Favoritos");;
		
		return rootView;
	}
}
