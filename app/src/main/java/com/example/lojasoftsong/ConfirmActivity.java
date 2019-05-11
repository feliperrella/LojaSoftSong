package com.example.lojasoftsong;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.Statement;

public class ConfirmActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Load();


        (findViewById(R.id.btnVisa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_visa).into(((ImageView) findViewById(R.id.selectP)));
                type = "Visa";
                generate();
            }
        });

        (findViewById(R.id.btnMaster)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Glide.with(getApplicationContext()).load(R.drawable.ic_mastercard).into(((ImageView) findViewById(R.id.selectP)));
                 type = "Master";
                 generate();
            }
        });

        (findViewById(R.id.btnPayPal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).load(R.drawable.ic_paypal).into(((ImageView) findViewById(R.id.selectP)));
                type = "PayPal";
                generate();
            }
        });

        (findViewById(R.id.btnBoleto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(getApplicationContext()).load(R.drawable.ic_boleto).into(((ImageView) findViewById(R.id.selectP)));
                type = "Boleto";
                generate();
            }
        });

        ((Button) findViewById(R.id.btnComprar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Compras().execute();
            }
        });

    }

    void Load()
    {
        for(int i = 0; i < CarrinhoActivity.Preco.size(); i++)
            t += Double.parseDouble(CarrinhoActivity.Preco.get(i));
        ((TextView) findViewById(R.id.txtEndereço)).setText("Endereço: " + MainActivity.sharedPref.getString("endereco", ""));
        ((TextView) findViewById(R.id.txtNome)).setText("Nome: " + MainActivity.sharedPref.getString("nome", ""));
        ((TextView) findViewById(R.id.txtValor)).setText("Valor: " + t);
    }

    void generate()
    {
        String text = type + ":" + MainActivity.sharedPref.getString("endereco", "") + "," + MainActivity.sharedPref.getString("nome", "") + t; // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ((ImageView) findViewById(R.id.qr)).setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    class Compras extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    //System.out.println("insert into tblPedido(ID_Cliente) values(,0,0," + MainActivity.sharedPref.getString("id", "") + ")");
                    stmt.executeUpdate("insert into tblPedido(ID_Cliente) values(" + MainActivity.sharedPref.getString("id", "") + ")" );
                    for(int i = 0; i < CarrinhoActivity.Nome.size(); i++)
                        stmt.executeUpdate("insert into tblDetalhePedido values((select count(*) from tblPedido), (select IDProduto from tblProduto where nome = " + CarrinhoActivity.Nome.get(i) + "))"  );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    ClasseConexao conexao = new ClasseConexao();
    String type = "";
    double t = 0;
}
