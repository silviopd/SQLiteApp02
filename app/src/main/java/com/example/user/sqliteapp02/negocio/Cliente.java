package com.example.user.sqliteapp02.negocio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.sqliteapp02.datos.AccesoDatos;

import java.util.ArrayList;

/**
 * Created by USER on 20/09/2016.
 */
public class Cliente extends AccesoDatos {

    private String dni;
    private String nombre;
    private String telefono;
    private String codigoDepartamento;
    private String codigoProvincia;
    private String codigoDistrito;

    private double latitud;
    private double longitud;

    private String ruta;

    public static ArrayList<Cliente> listaCli = new ArrayList<Cliente>();

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public long agregar(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("dni",this.getDni());
        valores.put("nombre",this.getNombre());
        valores.put("telefono",this.getTelefono());
        valores.put("codigo_departamento",this.getCodigoDepartamento());
        valores.put("codigo_provincia",this.getCodigoProvincia());
        valores.put("codigo_distrito",this.getCodigoDistrito());

        valores.put("latitud",this.getLatitud());
        valores.put("longitud",this.getLongitud());
        valores.put("ruta",this.getRuta());

        long resultado = db.insert("cliente",null,valores);

        return resultado;
    }

    public void cargarLista(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultado = db.rawQuery("select * from cliente order by nombre", null);
        listaCli.clear();
        while(resultado.moveToNext()){
            Cliente obj = new Cliente();
            obj.setNombre(resultado.getString(1));
            obj.setTelefono(resultado.getString(2));
            obj.setDni(resultado.getString(0));
            obj.setLatitud(resultado.getDouble(6));
            obj.setLongitud(resultado.getDouble(7));
            obj.setRuta(resultado.getString(8));
            listaCli.add(obj);
        }
    }

    public long eliminar(String dni){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cliente", "dni = '" + dni + "'", null);
    }

    public Cursor leerDatos(String dni){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "select " +
                        "c.dni, " +
                        "c.nombre, " +
                        "c.telefono, " +
                        "d.nombre, " +
                        "p.nombre, " +
                        "di.nombre," +
                        "c.codigo_departamento," +
                        "c.codigo_provincia, " +
                        "c.latitud," +
                        "c.longitud," +
                        "c.ruta "+
                        "from cliente c " +
                        "inner join distrito di on ( c.codigo_departamento = di.codigo_departamento and c.codigo_provincia = di.codigo_provincia and c.codigo_distrito = di.codigo_distrito ) " +
                        "inner join provincia p on ( di.codigo_departamento = p.codigo_departamento and di.codigo_provincia = p.codigo_provincia ) " +
                        "inner join departamento d on ( p.codigo_departamento = d.codigo_departamento ) " +
                        "where " +
                        "dni = '" + dni + "'";


        Cursor resultado = db.rawQuery(sql, null);
        return resultado;
    }

    public long editar(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        //valores.put("dni",this.getDni());
        valores.put("nombre",this.getNombre());
        valores.put("telefono",this.getTelefono());
        valores.put("codigo_departamento",this.getCodigoDepartamento());
        valores.put("codigo_provincia",this.getCodigoProvincia());
        valores.put("codigo_distrito",this.getCodigoDistrito());

        valores.put("latitud",this.getLatitud());
        valores.put("longitud",this.getLongitud());
        valores.put("ruta",this.getRuta());

        String condicion = "dni='"+this.getDni()+"'";

        long resultado = db.update("cliente",valores,condicion,null);

        return resultado;
    }

    public long editarUbicacion(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("latitud",this.getLatitud());
        valores.put("longitud",this.getLongitud());

        String condicion = "dni='"+this.getDni()+"'";

        long resultado = db.update("cliente",valores,condicion,null);

        return resultado;
    }
}
