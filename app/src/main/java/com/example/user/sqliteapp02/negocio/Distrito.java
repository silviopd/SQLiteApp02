package com.example.user.sqliteapp02.negocio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.sqliteapp02.datos.AccesoDatos;

import java.util.ArrayList;

/**
 * Created by USER on 15/09/2016.
 */
public class Distrito extends AccesoDatos {

    private String codigoDepartamento;
    private String codigoProvincia;
    private String codigoDistrito;
    private String nombre;

    public static ArrayList<Distrito> listaDis = new ArrayList<Distrito>();

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public String getCodigoDistrito() {
        return codigoDistrito;
    }

    public void setCodigoDistrito(String codigoDistrito) {
        this.codigoDistrito = codigoDistrito;
    }

    public String getCodigoProvincia() {
        return codigoProvincia;
    }

    public void setCodigoProvincia(String codigoProvincia) {
        this.codigoProvincia = codigoProvincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    private void cargarDatosDistrito(String codigoDepartamento,String codigoProvincia){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select distrito.codigo_departamento,distrito.codigo_provincia,distrito.codigo_distrito,distrito.nombre from distrito inner join provincia on distrito.codigo_departamento=provincia.codigo_departamento and distrito.codigo_provincia=provincia.codigo_provincia inner join departamento on departamento.codigo_departamento=provincia.codigo_departamento where distrito.codigo_departamento like '"+codigoDepartamento+"' AND distrito.codigo_provincia like '"+codigoProvincia+"'";
        Cursor resultado = bd.rawQuery(sql,null);

        listaDis.clear();

        while(resultado.moveToNext()){
            Distrito objPro = new Distrito();
            objPro.setCodigoDepartamento(resultado.getString(0));
            objPro.setCodigoProvincia(resultado.getString(1));
            objPro.setCodigoDistrito(resultado.getString(2));
            objPro.setNombre(resultado.getString(3));
            listaDis.add(objPro);
        }
    }

    public String[] listaDistrito(String codigoDepartamento,String codigoProvincia){
        cargarDatosDistrito(codigoDepartamento,codigoProvincia);

        String listaNombresDistritos[] = new String[listaDis.size()];

        for (int i = 0; i < listaDis.size(); i++) {
            Distrito item = listaDis.get(i);
            listaNombresDistritos[i] = item.getNombre();
        }

        return listaNombresDistritos;
    }
}
