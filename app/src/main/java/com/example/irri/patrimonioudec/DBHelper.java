package com.example.irri.patrimonioudec;

/**
 * Created by Irri on 27-06-2018.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBHelper {
    // Nombre de la base de datos, y tabla asociada
    private static final String DATABASE_NAME = "MiDB";
    private static final String DATABASE_TABLE = "puntos";
    private static final int DATABASE_VERSION = 1;
    // Las constantes que representan las columnas de la tabla
    private static final String FILAID = "id";
    private static final String LNG = "lng";
    private static final String LAT = "lat";
    private static final String NOMBRE = "nombre";
    private static final String NFOTOS = "nfotos";
    private static final String TAG = "DBHelper";
    // Este String contiene el comando SQL para la creación de la base de datos
    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE +
            "(" + FILAID + " integer primary key autoincrement, " + NOMBRE +
            " text not null, " + NFOTOS + " integer not null,"+ LAT +" real not null, " +
            LNG +" real not null " + ");";
    private final Context contexto; // Contexto de la aplicacion
    private DatabaseHelper Helper; // Clase interna para acceso a base de datos SQL
    private SQLiteDatabase db; // La base de datos SQL
    public DBHelper(Context contexto) {
        this.contexto = contexto;
        Helper = new DatabaseHelper(contexto);
    }
    // Clase privada interna para acceso a base de datos SQL
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private Context context;
        DatabaseHelper(Context contexto) {
            super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
            context = contexto;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.w(TAG, "Creando la base de datos");
                // Emite el comando SQL para crear la base de datos
                db.execSQL(DATABASE_CREATE);
                InputStream is = context.getResources().openRawResource(R.raw.puntitos);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine())!=null){
                    String[] words = line.split(" ");
                    double lat = Double.parseDouble(words[0]);
                    double lng = Double.parseDouble(words[1]);
                    int nfotos = Integer.parseInt(words[3]);

                    ContentValues valores = new ContentValues();
                    valores.put(NOMBRE, words[2]);
                    valores.put(LAT, lat);
                    valores.put(LNG, lng);
                    valores.put(NFOTOS, nfotos);
                    db.insert(DATABASE_TABLE, null, valores);
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
            Log.w(TAG, "Actualizando la base de datos desde la versión " + versionAnterior +
                    " a la versión " + versionNueva);
            db.execSQL("DROP TABLE IF EXISTS contactos");
            onCreate(db);
        }
    }
    // Abre la base de datos para escritura
    public DBHelper abre() throws SQLException {
        // Abre la base de datos para escritura
        db = Helper.getWritableDatabase();
        return this;
    }
    // Cierra la base de datos
    public void cierra() {
        Helper.close();
    }

    public Cursor getAll(){
        return db.query(DATABASE_TABLE, new String[] {FILAID,NFOTOS,LAT,LNG,NOMBRE},
                null,null,null, null, FILAID+" ASC");
    }

    public int getNFotos(int id){
        Cursor c = db.query(DATABASE_TABLE, new String[] {NFOTOS},
                FILAID+" = ? ",
                new String[]{Integer.toString(id)},null, null, null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public String getName(int id){
        Cursor c = db.query(DATABASE_TABLE, new String[] {NOMBRE},
                FILAID+" = ? ",
                new String[]{Integer.toString(id)},null, null, null);
        c.moveToFirst();
        return c.getString(0);
    }


}

