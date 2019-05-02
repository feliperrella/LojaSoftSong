package com.example.lojasoftsong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gelitenight.waveview.library.WaveView;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CarrinhoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_carrinho);
        new Load().execute();
        WaveView waveView = findViewById(R.id.waver);
        WaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setWaveColor(
                Color.parseColor("#B079E1"),
                Color.parseColor("#285FBAE6"));
        ((TextView) findViewById(R.id.nCarrinho)).setText(HomeActivity.Carrinho.size() + "");
        if (HomeActivity.Carrinho.size() == 0)
        {
            (findViewById(R.id.ButtonConfirma)).setEnabled(false);
            (findViewById(R.id.ButtonConfirma)).setBackgroundColor(Color.GRAY);
        }
        (findViewById(R.id.ButtonConfirma)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    class Load extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao conexao = new ClasseConexao();
            String a = "";
            double val = 0;
            for (int i = 0; i < HomeActivity.Carrinho.size(); i++)
                a += "'" + HomeActivity.Carrinho.get(i) + "',";
            if (a.length() > 0)
                a = a.substring(0, a.length() - 1);
            String x = "Select nome,IDProduto,descricao,preco_unitario,caminho_imagem Quantidade from tblProduto where IDProduto in (" + a + ")";
            try {
                Connection connection = conexao.CONN();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(x);
                rs.beforeFirst();
                while(rs.next())
                {
                    Nome.add(rs.getString("nome"));
                    Preco.add(rs.getString("preco_unitario"));
                    Caminho_Imagem.add(rs.getString("caminho_imagem"));
                }
            }
            catch (Exception e) {}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adaptador Adaptador= new adaptador();
            ((ListView) findViewById(R.id.lista)).setAdapter(Adaptador);
        }
    }

    class adaptador extends BaseAdapter
    {
        @Override
        public int getCount() {
            return Nome.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_produtos_linha, null);
            }
            final ImageView pic = convertView.findViewById(R.id.ProdPic);
            final TextView name = convertView.findViewById(R.id.ProdName);
            final TextView price = convertView.findViewById(R.id.ProdVal);
            Thread x = new Thread(){
                @Override
                public void run() {
                    super.run();
                    Glide.with(getApplicationContext()).load(R.drawable.teste).into(pic);
                    name.setText(Nome.get(position));
                    price.setText(Preco.get(position).substring(0, Preco.get(position).length() - 2));
                }
            };
            x.run();
            return convertView;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WaveHelper.start();
        ((TextView) findViewById(R.id.nCarrinho)).setText(HomeActivity.Carrinho.size() + "");
        if (HomeActivity.Carrinho.size() == 0)
        {
            (findViewById(R.id.ButtonConfirma)).setEnabled(false);
            (findViewById(R.id.ButtonConfirma)).setBackgroundColor(Color.GRAY);
        }
    }
    private WaveHelper WaveHelper;
    private static ArrayList<String> Nome = new ArrayList<>(), Preco = new ArrayList<>(), Caminho_Imagem = new ArrayList<>();
}
