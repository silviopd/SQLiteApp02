package com.example.user.sqliteapp02;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.sqliteapp02.datos.AccesoDatos;
import com.example.user.sqliteapp02.negocio.Cliente;
import com.example.user.sqliteapp02.util.Funciones;

import java.util.ArrayList;

public class ClienteListado extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fab;
    ListView lvLista;

    ArrayList<Cliente> lista;
    ClienteAdaptador adaptador;

    private static final int REQUEST_CODE = 0;
    private double latitud=0, longitud=0;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_listado);

        AccesoDatos.aplicacion = this;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        lvLista = (ListView) findViewById(R.id.lvListado);

        registerForContextMenu(lvLista);

        listar();
    }

    private void listar() {
        new Cliente().cargarLista();
        lista = new ArrayList<Cliente>();
        lista = Cliente.listaCli;
        adaptador = new ClienteAdaptador(this, lista);
        lvLista.setAdapter(adaptador);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        listar();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvListado) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(Cliente.listaCli.get(info.position).getNombre());
            String[] menuItems = getResources().getStringArray(R.array.menu);    //menu del strings.xml
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        switch (menuItemIndex) {
            case 0:  //editar
                Intent i = new Intent(this,MainActivity.class);
                Bundle p = new Bundle();
                p.putInt("position",info.position);
                this.position = info.position;
                i.putExtras(p);
                startActivity(i);

                break;
            case 1:  //eliminar

                boolean r = Funciones.mensajeConfirmacion(this, "Confirme", "Desea eliminar");
                if (r == false) {
                    return false;
                }

                String dni = Cliente.listaCli.get(info.position).getDni();
                long resultado = new Cliente().eliminar(dni);
                if (resultado > 0) {
                    Toast.makeText(this, "Registro eliminado", Toast.LENGTH_LONG).show();
                    listar();
                }
                break;
            case 2: //Ver en el mapa
                Intent m = new Intent(this, cliente_mapa_y_mapaasignar.class);
                Bundle pm = new Bundle();
                pm.putInt("position", info.position);
                m.putExtras(pm);
                //startActivity(m);
                startActivityForResult(m, REQUEST_CODE);
                break;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                Bundle p = data.getExtras();
                this.latitud = p.getDouble("latitud");
                this.longitud = p.getDouble("longitud");

                Cliente objCliente = new Cliente();
                objCliente.setLatitud(this.latitud);
                objCliente.setLongitud(this.longitud);
                objCliente.setDni(Cliente.listaCli.get(this.position).getDni());
                objCliente.editarUbicacion();

                Toast.makeText(this, "Actualizando la ubicación del cliente\n\n" + "Lat: " + this.latitud + ", Long: " + this.longitud, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
