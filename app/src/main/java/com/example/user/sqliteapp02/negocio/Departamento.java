package com.example.user.sqliteapp02.negocio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.sqliteapp02.datos.AccesoDatos;

import java.util.ArrayList;

/**
 * Created by USER on 15/09/2016.
 */
public class Departamento extends AccesoDatos {

    private String codigoDepartamento;
    private String nombre;

    public static ArrayList<Departamento> listaDep = new ArrayList<Departamento>();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    private void cargarDatosDepartamento(){
        SQLiteDatabase bd = this.getReadableDatabase();
        String sql = "select * from departamento order by 2";
        Cursor resultado = bd.rawQuery(sql,null);

        listaDep.clear();

        while(resultado.moveToNext()){
            Departamento objDep = new Departamento();
            objDep.setCodigoDepartamento(resultado.getString(0));
            objDep.setNombre(resultado.getString(1));
            listaDep.add(objDep);
        }
    }

    public String[] listaDespartamento(){
        cargarDatosDepartamento();

        String listaNombresDepartamentos[] = new String[listaDep.size()];

        for (int i = 0; i < listaDep.size(); i++) {
            Departamento item = listaDep.get(i);
            listaNombresDepartamentos[i] = item.getNombre();
        }

        return listaNombresDepartamentos;
    }
}
