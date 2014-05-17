package com.example.kitchapp;

import java.util.ArrayList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MostrarProductosCategoria extends ActionBarActivity implements
		OnClickListener {

	private ListView list;
	private ArrayList<ItemProducto> products;
	private int pos;
	private boolean pressedButtonDelete = false;
	private MenuItem item_add;
	private EditText cantProduct;
	private EditText nameProduct;
	private Button save;
	private Button cancel;
	private Button decrement;
	private Button increment;
	private AlertDialog.Builder builder;
	private Integer tipoCat;
	private static final int REQUEST_CODE = 1234;
	Handler_Sqlite helper;
	Dialog match_text_dialog;
	ListView textlist;
	ArrayList<String> matches_text;
	private boolean barCode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mostrar_productos_categoria);
		helper = new Handler_Sqlite(this);
		products = new ArrayList<ItemProducto>();

		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			tipoCat = extras.getInt("idCat");
			SQLiteDatabase tmp = helper.open();
			if (tmp != null) {
				initializeArrayList(tipoCat);
				helper.close();
			}
			TextView title = (TextView) findViewById(R.id.textView_Cat);
			switch (tipoCat) {
			case 1:
				title.setText("Lacteos");
				break;

			case 2:
				title.setText("Frutas y Verduras");
				break;

			case 3:
				title.setText("Pan y Bolleria");
				break;

			case 4:
				title.setText("Bebidas");
				break;

			case 5:
				title.setText("Carnes");
				break;

			case 6:
				title.setText("Pescados");
				break;

			case 7:
				title.setText("Condimentos");
				break;

			case 8:
				title.setText("Pastas y Arroces");
				break;

			case 9:
				title.setText("Congelados");
				break;

			case 10:
				title.setText("Salsas");
				break;

			case 11:
				title.setText("Drogueria");
				break;

			case 12:
				title.setText("Varios");
				break;

			}
		}
		list = (ListView) findViewById(R.id.listViewProducts);
		ItemProductoAdapter adapter;
		// Inicializamos el adapter.
		adapter = new ItemProductoAdapter(this, products);
		// Asignamos el Adapter al ListView, en este punto hacemos que el
		// ListView muestre los datos que queremos.
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				modificarProducto(arg1, position);

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_action_bar, menu);
		item_add = menu.findItem(R.id.add_Product);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.add_Product:
			alertDialogListView(true, 0);
			return true;

		case R.id.delete_Product:
			if (pressedButtonDelete) {
				item_add.setEnabled(true);
				deleteProducts();
				hideCheckBox();
				pressedButtonDelete = false;
			} else {
				item_add.setEnabled(false);
				showCheckBox();
				pressedButtonDelete = true;
			}
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void modificarProducto(View view, int position) {

		builder = new AlertDialog.Builder(this);

		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		view = inflater.inflate(R.layout.activity_modificar_producto_despensa,
				null);
		nameProduct = (EditText) view.findViewById(R.id.nameProductModify);
		nameProduct.setText(products.get(position).getNombre());
		cantProduct = (EditText) view.findViewById(R.id.cantProduct);
		cantProduct.setText(products.get(position).getCantidad() + "");
		pos = position;
		save = (Button) view.findViewById(R.id.button_save);
		save.setOnClickListener(this);
		cancel = (Button) view.findViewById(R.id.button_cancel);
		cancel.setOnClickListener(this);
		decrement = (Button) view.findViewById(R.id.button_decrement);
		decrement.setOnClickListener(this);
		increment = (Button) view.findViewById(R.id.button_increment);
		increment.setOnClickListener(this);
		builder.setView(view);

		builder.create();
		builder.show();

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_save:
			ItemProducto prod = products.get(pos);
			String nameLast = prod.getNombre();
			prod.setNombre(nameProduct.getText().toString());
			prod.setCantidad(Integer.parseInt(cantProduct.getText().toString()));
			products.set(pos, prod);
			SQLiteDatabase tmp = helper.open();
			if (tmp != null) {
				helper.updateProduct(nameLast, prod.getNombre(),
						prod.getCantidad());
				helper.close();
			}
			Intent j = new Intent(this, MostrarProductosCategoria.class);
			j.putExtra("idCat", tipoCat);
			startActivity(j);

			break;

		case R.id.button_cancel:
			Intent i = new Intent(this, MostrarProductosCategoria.class);
			i.putExtra("idCat", tipoCat);
			startActivity(i);
			break;

		/*
		 * case R.id.textView_Atras: Intent j = new
		 * Intent(this,AccesoDespensa.class); startActivity(j); break;
		 */

		case R.id.button_decrement:
			decrementCant(v);
			break;

		case R.id.button_increment:
			incrementCant(v);
			break;

		/*
		 * case R.id.button_add: alertDialogListView(true,0); break;
		 */
		}

	}

	private void initializeArrayList(Integer category) {

		products = helper.readProducts(category, "readPantry");

		/*
		 * products.add(new ItemProducto(1,helper.read()[1],4,""));
		 * products.add(new ItemProducto(2,helper.read()[2],3,""));
		 * products.add(new ItemProducto(3,helper.read()[4],4,""));
		 * products.add(new ItemProducto(4,"Natillas Chocolate Danone",2,""));
		 * products.add(new ItemProducto(5,"Queso Semicurado El Ventero",1,""));
		 */

	}

	public void decrementCant(View view) {
		/*
		 * cantProduct = (TextView) view.findViewById(R.id.cantProduct); int
		 * cant = Integer.parseInt(cantProduct.getText().toString()); int
		 * cantModified = cant--; cantProduct.setText(cantModified + "");
		 */

		if (Integer.parseInt(cantProduct.getText().toString()) > 0) {
			cantProduct.setText(Integer.parseInt(cantProduct.getText()
					.toString()) - 1 + "");

		}

	}

	public void incrementCant(View view) {
		/*
		 * cantProduct = (TextView) view.findViewById(R.id.cantProduct); int
		 * cant = Integer.parseInt(cantProduct.getText().toString()); int
		 * cantModified = cant--; cantProduct.setText(cantModified + "");
		 */

		cantProduct.setText(Integer.parseInt(cantProduct.getText().toString())
				+ 1 + "");

	}

	public void modifyProduct() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Informacion")
				.setIcon(
						getResources().getDrawable(
								android.R.drawable.ic_dialog_info))
				.setMessage("Producto ya existente en la despensa")
				.setNeutralButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
							}
						});

		builder.create();
		builder.show();
	}

	public void alertDialogListView(boolean full, int except) {
		if (full & except == 0) {
			final String[] items = new String[] { "Manualmente", "Voz",
					"Codigo de barras" };
			final Integer[] icons = new Integer[] { R.drawable.teclado_android,
					R.drawable.microfono, R.drawable.barras };
			ListAdapter adapter = new ItemAdapter(this, items, icons);

			new AlertDialog.Builder(this).setAdapter(adapter,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item == 0)
								addManualmente();
							else if (item == 1) 
								addVoice();
							else if (item == 2) {
								barCode = true;
								addBarCode();
							}						
						}
							
						
					}).show();
		} else {
			if (except == 1) {
				final String[] items = new String[] { "Voz", "Codigo de barras" };
				final Integer[] icons = new Integer[] { R.drawable.microfono,
						R.drawable.barras };
				ListAdapter adapter = new ItemAdapter(this, items, icons);

				new AlertDialog.Builder(this).setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0) {
									// barCode = false;
									addVoice();
								} else if (item == 1) {
									barCode = true;
									addBarCode();
								}
							}
						}).show();
			} else if (except == 2) {
				final String[] items = new String[] { "Manualmente",
						"Codigo de barras" };
				final Integer[] icons = new Integer[] {
						R.drawable.teclado_android, R.drawable.barras };
				ListAdapter adapter = new ItemAdapter(this, items, icons);

				new AlertDialog.Builder(this).setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0)
									addManualmente();
								else if (item == 1) {
									barCode = true;
									addBarCode();
								}
							}
						}).show();
			} else if (except == 3) {
				final String[] items = new String[] { "Manualmente", "Voz" };
				final Integer[] icons = new Integer[] {
						R.drawable.teclado_android, R.drawable.microfono };
				ListAdapter adapter = new ItemAdapter(this, items, icons);

				new AlertDialog.Builder(this).setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0)
									addManualmente();
								else if (item == 1) {
									// barCode = false;
									addVoice();
								}
							}
						}).show();
			}
		}

	}

	public void addManualmente() {
		Intent intent = new Intent(this, AddManualmente.class);
		intent.putExtra("idCat", tipoCat);
		startActivity(intent);
	}

	public void addVoice() {
		if (isConnected()) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			startActivityForResult(intent, REQUEST_CODE);
		} else {
			Toast.makeText(getApplicationContext(),
					"Please Connect to Internet", Toast.LENGTH_LONG).show();
		}
	}

	public void addBarCode() {
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = cm.getActiveNetworkInfo();
		if (net != null && net.isAvailable() && net.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!barCode) {
			if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

				match_text_dialog = new Dialog(MostrarProductosCategoria.this);
				match_text_dialog.setContentView(R.layout.dialog_matches);
				match_text_dialog.setTitle("Select Matching Text");
				textlist = (ListView) match_text_dialog
						.findViewById(R.id.listDialogVoice);
				matches_text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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

		// BarCode

		else {
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, data);

			if (scanningResult != null) {
				// we have a result
				String result = scanningResult.getContents();
				if (!helper.exist(result, "products")) {
					if (!helper.exist(result, "productsTemporary")) {
						alertDialogReport("Producto no existente");
						// alertDialogListView(false,3);

					} else {
						ArrayList<Object> tmp = helper
								.readProductsTemporary(result);
						helper.insertProducts((String) tmp.get(0), 1, tipoCat,
								(String) tmp.get(1), "insertPantry", 1);
						// Para que se refresque la informaci�n en la pantalla
						ItemProducto item = new ItemProducto(products.size(),
								(String) tmp.get(0), 1, 1, false);
						products.add(item);
						list = (ListView) findViewById(R.id.listViewProducts);
						ItemProductoAdapter adapter;
						// Inicializamos el adapter.
						adapter = new ItemProductoAdapter(this, products);
						// Asignamos el Adapter al ListView, en este punto
						// hacemos que el
						// ListView muestre los datos que queremos.
						list.setAdapter(adapter);

					}

				} else {
					Toast.makeText(this, "Producto ya existente",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"No scan data received!", Toast.LENGTH_SHORT);
				toast.show();
			}

			// fin BarCode
			barCode = false;		}

	}

	public void alertDialogReport(String msj) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// builder.setTitle("Error")
		builder.setIcon(getResources().getDrawable(R.drawable.close))
				.setMessage(msj)
				.setNeutralButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// arg0.cancel();
								alertDialogListView(false, 3);
							}
						});

		builder.create();
		builder.show();
	}

	public void addProduct(int position) {
		String[] prod = matches_text.get(position).split(" ");
		String name = "";
		String oneCantM = "un";
		String oneCantF = "una";
		int cant = 0;
		boolean error = false;
		// Intent intent = new Intent(this,MostrarProductosCategoria.class);
		for (int i = 0; i < prod.length; i++) {
			if (cant == 0) {
				if ((prod[i].equals(oneCantM)) || (prod[i].equals(oneCantF))) {
					cant = 1;
				} else {
					try {
						cant = Integer.parseInt(prod[i]);
					} catch (NumberFormatException e) {
						if (i == prod.length - 1) {
							errorCantVoice();
							error = true;
						} else {
							name += prod[i];
							name += " ";
						}
					}
				}
			} else {
				name += prod[i];
				name += " ";
			}

		}
		if (!error) {
			if (cant <= 0) {
				errorCantVoice();
			} else {
				initializeArrayList(tipoCat);
				ItemProducto item = new ItemProducto(products.size(), name,
						cant, 1, false);
				boolean encontrado = false;
				int i = 0;
				int cantProductModify = 0;
				while (i < products.size() && !encontrado) {
					ItemProducto product = products.get(i);
					String nameP = product.getNombre().toLowerCase();
					if (nameP.equals(item.getNombre().toLowerCase())) {
						encontrado = true;
						cantProductModify = product.getCantidad()
								+ item.getCantidad();
					}
					i++;
				}
				if (!encontrado) {
					products.add(item);
					SQLiteDatabase tmp = helper.open();
					if (tmp != null) {
						helper.insertProducts(item.getNombre(),
								item.getCantidad(), tipoCat, "",
								"insertPantry", 1);
						helper.close();
					}

				} else {
					modifyProduct();
					products.get(i - 1).setCantidad(cantProductModify);
					SQLiteDatabase tmp = helper.open();
					if (tmp != null) {
						helper.updateProduct(item.getNombre(),
								item.getNombre(), cantProductModify);
						helper.close();
					}
				}

				list = (ListView) findViewById(R.id.listViewProducts);
				ItemProductoAdapter adapter = new ItemProductoAdapter(this,
						products);
				list.setAdapter(adapter);
			}
		}

	}

	public void errorCantVoice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Error")
				.setIcon(getResources().getDrawable(R.drawable.close))
				.setMessage(
						"La cantidad del producto introducida tiene que ser un numero mayor que cero")
				.setNeutralButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
							}
						});

		builder.create();
		builder.show();
	}

	public void showCheckBox() {
		for (int i = 0; i < list.getChildCount(); i++) {
			View v = list.getChildAt(i);
			CheckBox check = (CheckBox) v.findViewById(R.id.checkBox_Product);
			check.setVisibility(View.VISIBLE);
		}
	}

	public void hideCheckBox() {
		for (int i = 0; i < list.getChildCount(); i++) {
			View v = list.getChildAt(i);
			CheckBox check = (CheckBox) v.findViewById(R.id.checkBox_Product);
			check.setVisibility(View.INVISIBLE);
		}
	}

	public void deleteProducts() {
		for (int i = 0; i < products.size(); i++) {
			ItemProducto item = products.get(i);
			if (item.isSelected()) {
				products.remove(i);
				i--;
				SQLiteDatabase tmp = helper.open();
				if (tmp != null) {
					helper.removeProduct(item.getNombre(), "deletePantry", 1);
					helper.close();
				}
			}
		}
		list = (ListView) findViewById(R.id.listViewProducts);
		ItemProductoAdapter adapter = new ItemProductoAdapter(this, products);
		list.setAdapter(adapter);
	}

}
