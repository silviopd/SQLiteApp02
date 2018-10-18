package com.example.user.sqliteapp02;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.sqliteapp02.datos.AccesoDatos;
import com.example.user.sqliteapp02.negocio.Cliente;
import com.example.user.sqliteapp02.negocio.Departamento;
import com.example.user.sqliteapp02.negocio.Distrito;
import com.example.user.sqliteapp02.negocio.Provincia;
import com.example.user.sqliteapp02.util.Funciones;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnTouchListener {

    Spinner spDepartamento, spProvincia, spDistrito;
    EditText txtdni, txtnombre, txttelefono;
    Button btnGrabar, btnUbicacion;
    ImageView btnImg;

    boolean userSelect;

    //
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    private String imgPath = "";

    private LinearLayout mRlView;
    //

    //
    private double latitud = 0, longitud = 0;
    private int position;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spDepartamento = (Spinner) findViewById(R.id.spDepartamento);
        spProvincia = (Spinner) findViewById(R.id.spProvincia);
        spDistrito = (Spinner) findViewById(R.id.spDistrito);

        txtdni = (EditText) findViewById(R.id.txtDni);
        txtnombre = (EditText) findViewById(R.id.txtNombre);
        txttelefono = (EditText) findViewById(R.id.txtTelefono);

        btnGrabar = (Button) findViewById(R.id.btnGrabar);
        btnUbicacion = (Button) findViewById(R.id.btnUbicacion);
        btnImg = (ImageView) findViewById(R.id.btnImg);

        AccesoDatos.aplicacion = this;

        spDepartamento.setOnItemSelectedListener(this);
        spProvincia.setOnItemSelectedListener(this);
        spDistrito.setOnItemSelectedListener(this);

        spDepartamento.setOnTouchListener(this);
        spProvincia.setOnTouchListener(this);

        btnGrabar.setOnClickListener(this);
        btnUbicacion.setOnClickListener(this);

        //registerForContextMenu(btnImg);
        btnImg.setOnClickListener(this);

        mRlView = (LinearLayout) findViewById(R.id.rl_view);

        cargarDatosSpinnerDepartamento();

        Bundle p = this.getIntent().getExtras();
        if (p != null) {//Esta llegando un parametro, significa que debo leer los datos
            this.userSelect = false;

            this.position = p.getInt("position");
            Cliente item = Cliente.listaCli.get(position);
            this.leerDatos(item.getDni());

            if (!imgPath.isEmpty()) {
                Uri path = Uri.parse(imgPath);
                btnImg.setImageURI(path);
            } else {
                btnImg.setImageResource(R.drawable.foto);
            }
            System.out.println("medio");
        }else{
            this.userSelect = true;
        }


        if (myRequestStoragePermission()) {
            btnImg.setEnabled(true);
        } else {
            btnImg.setEnabled(false);
        }
    }

    private boolean myRequestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicacion", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void leerDatos(String dni) {
        Cursor cursor = new Cliente().leerDatos(dni);
        if (cursor.moveToNext()) {
            txtdni.setText(cursor.getString(0));
            txtnombre.setText(cursor.getString(1));
            txttelefono.setText(cursor.getString(2));

            this.cargarDatosSpinnerProvincia(cursor.getString(6));
            this.cargarDatosSpinnerDistrito(cursor.getString(6), cursor.getString(7));

            Funciones.selectedItemSpinner(spDepartamento, cursor.getString(3));
            Funciones.selectedItemSpinner(spProvincia, cursor.getString(4));
            Funciones.selectedItemSpinner(spDistrito, cursor.getString(5));

            this.latitud = cursor.getDouble(8);
            this.longitud = cursor.getDouble(9);
            this.imgPath = cursor.getString(10);

            txtdni.setEnabled(false);
        }
    }

    private void cargarDatosSpinnerDepartamento() {
        String listaDepartamento[] = new Departamento().listaDespartamento();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaDepartamento);

        spDepartamento.setAdapter(adapter);
    }

    private void cargarDatosSpinnerProvincia(String codigoDepartamento) {
        String listaProvincia[] = new Provincia().listaProvincia(codigoDepartamento);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaProvincia);

        spProvincia.setAdapter(adapter);
    }

    private void cargarDatosSpinnerDistrito(String codigoDepartamento, String codigoProvincia) {
        String listaDistrito[] = new Distrito().listaDistrito(codigoDepartamento, codigoProvincia);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaDistrito);

        spDistrito.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (this.userSelect) {

            this.userSelect = false;

            switch (adapterView.getId()) {
                case R.id.spDepartamento:
                    String dep = Departamento.listaDep.get(i).getCodigoDepartamento();
                    cargarDatosSpinnerProvincia(dep);
                    this.userSelect=true;
                    break;
                case R.id.spProvincia:
                    String dep2 = Provincia.listaPro.get(i).getCodigoDepartamento();
                    String pro2 = Provincia.listaPro.get(i).getCodigoProvincia();
                    cargarDatosSpinnerDistrito(dep2, pro2);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnGrabar:

                String dni = txtdni.getText().toString();
                String nombre = txtnombre.getText().toString();
                String telefono = txttelefono.getText().toString();

                //String codigoDepartamento = Distrito.listaDis.get(spDepartamento.getSelectedItemPosition()).getCodigoDepartamento();
                //String codigoProvincia= Distrito.listaDis.get(spProvincia.getSelectedItemPosition()).getCodigoProvincia();
                //String codigoDistrito = Distrito.listaDis.get(spDistrito.getSelectedItemPosition()).getCodigoDistrito();

                String codigoDepartamento = Distrito.listaDis.get(spDistrito.getSelectedItemPosition()).getCodigoDepartamento();
                String codigoProvincia = Distrito.listaDis.get(spDistrito.getSelectedItemPosition()).getCodigoProvincia();
                String codigoDistrito = Distrito.listaDis.get(spDistrito.getSelectedItemPosition()).getCodigoDistrito();

                if (dni.isEmpty()) {
                    Toast.makeText(this, "Ingrese dni", Toast.LENGTH_LONG).show();
                    txtdni.requestFocus();
                    return;
                }

                if (dni.length() < 8) {
                    Toast.makeText(this, "Ingrese dni de 8 digitos", Toast.LENGTH_LONG).show();
                    txtdni.requestFocus();
                    return;
                }

                if (nombre.isEmpty()) {
                    Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_LONG).show();
                    txtnombre.requestFocus();
                    return;
                }

                if (telefono.isEmpty()) {
                    Toast.makeText(this, "Ingrese telefono", Toast.LENGTH_LONG).show();
                    txttelefono.requestFocus();
                    return;
                }

                if (this.latitud == 0 || this.longitud == 0) {
                    Toast.makeText(this, "Seleccione su posicion", Toast.LENGTH_LONG).show();
                    btnUbicacion.callOnClick();
                    return;
                }

                Cliente obj = new Cliente();
                obj.setDni(dni);
                obj.setNombre(nombre);
                obj.setTelefono(telefono);
                obj.setCodigoDepartamento(codigoDepartamento);
                obj.setCodigoProvincia(codigoProvincia);
                obj.setCodigoDistrito(codigoDistrito);

                obj.setLatitud(this.latitud);
                obj.setLongitud(this.longitud);


                if (!imgPath.isEmpty()) {
                    obj.setRuta(this.imgPath);
                }else {
                    obj.setRuta("");
                }

                long resultado = -1;
                if (txtdni.isEnabled()) {
                    resultado = obj.agregar();
                } else {
                    resultado = obj.editar();
                }

                System.out.println("Resultado: " + resultado);

                if (resultado != -1) {
                    Toast.makeText(this, "Grabado Ok!", Toast.LENGTH_LONG).show();
                    this.finish();
                }

                break;

            case R.id.btnUbicacion:
                Intent m = new Intent(this, cliente_mapa_y_mapaasignar.class);
                Bundle pm = new Bundle();
                if (this.txtdni.isEnabled()){
                    pm.putInt("position", -1); //cliente nuevo
                }else{
                    pm.putInt("position", position);
                }

                m.putExtras(pm);
                startActivityForResult(m, REQUEST_CODE);
                break;

            case R.id.btnImg:

                showOpciones();

                break;
        }
    }

    private void showOpciones() {
        final CharSequence[] option = {"Tomar Foto", "Elegir imagen de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Elija una opcion");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which] == "Tomar Foto") {
                    openCamara();
                } else if (option[which] == "Elegir imagen de galeria") {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openCamara() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdirs();
        }

        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            imgPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(imgPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));

            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_CODE:

                    MediaScannerConnection.scanFile(this, new String[]{imgPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned path -> " + path + " :");
                            Log.i("ExternalStorage", "-> Uri =" + uri);
                        }
                    });



                    //Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    //btnImg.setImageBitmap(bitmap);

                    try {
                        ExifInterface exif = new ExifInterface(imgPath);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);
                        Matrix matrix = new Matrix();
                        if (rotation != 0f) {
                            matrix.preRotate(rotationInDegrees);
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        btnImg.setImageBitmap(bitmap);
                        btnImg.setImageBitmap(rotatedBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /*
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90.0f);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

                    btnImg.setImageBitmap(bitmap);
                    btnImg.setImageBitmap(rotatedBitmap);
                    */

                    //Toast.makeText(this,imgPath,Toast.LENGTH_LONG).show();
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    imgPath = String.valueOf(path);
                    btnImg.setImageURI(path);

                    //Toast.makeText(this,imgPath,Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_CODE:
                    Bundle p = data.getExtras();
                    this.latitud = p.getDouble("latitud");
                    this.longitud = p.getDouble("longitud");
                    //Toast.makeText(this, "Se ha capturado la ubicación del cliente\n\n" +"Lat: " + this.latitud + ", Long: " + this.longitud, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos Aceptados", Toast.LENGTH_LONG).show();
                btnImg.setEnabled(true);
            }
        } else {
            showExPlanation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("file_path", imgPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imgPath = savedInstanceState.getString("file_path");
    }

    private void showExPlanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permisos Denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.userSelect = true;
        return false;
    }

}
