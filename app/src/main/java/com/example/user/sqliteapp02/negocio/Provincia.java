package com.example.user.sqliteapp02.negocio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.sqliteapp02.datos.AccesoDatos;

import java.util.ArrayList;

/**
 * Created by USER on 15/09/2016.
 */
public class Provincia extends AccesoDatos{

    private String codigoDepartamento;
    private String codigoProvincia;
    private String nombre;

    public static ArrayList<Provincia> listaPro = new ArrayList<Provincia>();

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
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

    private void cargarDatosProvincia(String codigoDepartamento){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from provincia where codigo_departamento like '"+codigoDepartamento+"'";
        Cursor resultado = bd.rawQuery(sql,null);

        listaPro.clear();

        while(resultado.moveToNext()){
            Provincia objPro = new Provincia();
            objPro.setCodigoDepartamento(resultado.getString(0));
            objPro.setCodigoProvincia(resultado.getString(1));
            objPro.setNombre(resultado.getString(2));
            listaPro.add(objPro);
        }
    }

    public String[] listaProvincia(String codigoDepartamento){
        cargarDatosProvincia(codigoDepartamento);

            String listaNombresProvincia[] = new String[listaPro.size()];

            for (int i = 0; i < listaPro.size(); i++) {
                Provincia item = listaPro.get(i);
            listaNombresProvincia[i] = item.getNombre();
        }

        return listaNombresProvincia;
    }
}
