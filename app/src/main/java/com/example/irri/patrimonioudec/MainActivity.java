package com.example.irri.patrimonioudec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DBHelper db;
    private ImageView principal;
    private String nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);
        Intent i = getIntent();
        int id = i.getIntExtra("id",-1);
        if(id==-1){
            Log.e("error","no hay id asociada");
        }
        db.abre();
        nombre = db.getName(id);
        int idString = getResources().getIdentifier(nombre,"string",getPackageName());
        String titulo = getString(idString);
        TextView tv1 =findViewById(R.id.textView2);
        LinearLayout ll = findViewById(R.id.llayout);
        tv1.setText(titulo);
        principal = findViewById(R.id.imageView);
        int fprinc = getResources().getIdentifier(nombre+1,"drawable",getPackageName());
        principal.setImageResource(fprinc);
        TextView tv2 = findViewById(R.id.textView3);
        int idTexto = getResources().getIdentifier("t"+nombre,"string",getPackageName());
        String texto = getString(idTexto);
        tv2.setText(texto);
        //ll.addView(new Space(this));
        int nfotos = db.getNFotos(id);
        for(int j=1;j<=nfotos;j++){
            int idFoto = getResources().getIdentifier(nombre+j,"drawable",getPackageName());
            ImageView iv = new ImageView(this);
            iv.setImageResource(idFoto);
            ll.addView(iv);
            iv.setAdjustViewBounds(true);
            iv.setPadding(40,20,40,20);
            iv.setOnClickListener(new ImageClickListener(j));
            //ll.addView(new Space(this));
        }
        db.cierra();

    }

    private class ImageClickListener implements View.OnClickListener{
        int id;
        public ImageClickListener(int id){
            this.id=id;
        }
        @Override
        public void onClick(View view) {
            int fprinc = getResources().getIdentifier(nombre+id,"drawable",getPackageName());
            principal.setImageResource(fprinc);

        }
    }
}
