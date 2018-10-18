package com.example.user.sqliteapp02;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sqliteapp02.negocio.Cliente;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.user.sqliteapp02.R.id.btnImg;


public class ClienteAdaptador extends BaseAdapter {

    public static ArrayList<Cliente> lista;
    private LayoutInflater layoutInflater;

    public ClienteAdaptador(Context context, ArrayList<Cliente> lista) {
        this.layoutInflater = LayoutInflater.from(context);
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {  //solo se ve lo q se va scrolleando
            convertView = layoutInflater.inflate(R.layout.cliente_item, null);
            holder = new Holder();
            holder.lblNombre = (TextView) convertView.findViewById(R.id.lblNombre);
            holder.lblTelefono = (TextView) convertView.findViewById(R.id.lblTelefono);
            holder.imgImagen = (ImageView) convertView.findViewById(R.id.imgImagen);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();      //solo lo lee sin recargar
        }

        Cliente item = Cliente.listaCli.get(position);
        holder.lblNombre.setText(item.getNombre());
        holder.lblTelefono.setText(item.getTelefono());
        //holder.imgImagen.setImageResource(R.drawable.foto);

        //holder.imgImagen.setImageURI(Uri.parse(item.getRuta()));

        //File exists = new File(item.getRuta());

        if (item.getRuta().isEmpty()){

            //if (item.getRuta().isEmpty() && !exists.isDirectory() && !exists.isFile() || !item.getRuta().isEmpty() && !exists.isDirectory() && !exists.isFile()){

                holder.imgImagen.setImageResource(R.drawable.foto);
            }else{
                holder.imgImagen.setImageURI(Uri.parse(item.getRuta()));
            }

        return convertView;
    }

    private class Holder{   //para llamar a los controles
        TextView lblNombre;
        TextView lblTelefono;
        ImageView imgImagen;
    }


}
