package com.example.lojasoftsong;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PerfilActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_perfil);
        new Load().execute();
        ((TextView) findViewById(R.id.Nome)).setText(MainActivity.sharedPref.getString("nome", ""));
        ((ImageView) findViewById(R.id.configs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(PerfilActivity.this, ConfigActivity.class);
                startActivity(x, ActivityOptions.makeSceneTransitionAnimation(PerfilActivity.this).toBundle());
            }
        });
    }

    class Load extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao classeConexao = new ClasseConexao();
            try {
                Connection connection = classeConexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("select * from tblPedido where ID_Cliente = " + MainActivity.sharedPref.getString("id", ""));
                    rs.beforeFirst();
                    while (rs.next())
                    {
                        mCodigo.add(rs.getString("IDPedido"));
                        mNames.add("Pedido Realizado");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Adapter adapt = new Adapter();
            ((ListView) findViewById(R.id.lista)).setAdapter(adapt);
        }
    }

    class Adapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return mCodigo.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_produtos_linha, null);
            //Glide.with(getApplicationContext()).load(R.drawable.teste).into((ImageView) view.findViewById(R.id.ProdPic));
            ((TextView) view.findViewById(R.id.ProdName)).setText(mNames.get(i));
            ((TextView) view.findViewById(R.id.ProdVal)).setText("Codigo: " + mCodigo.get(i));
            return view;
        }
    }
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mCodigo = new ArrayList<>();
    private ArrayList<String> mPreco = new ArrayList<>();
}
