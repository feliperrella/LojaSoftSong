package com.example.lojasoftsong;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConfigActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        new Load().execute();
        ((Button) findViewById(R.id.atualizar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Atualiza().execute();
            }
        });
    }

    class Load extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao conexao = new ClasseConexao();
            try
            {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("select * from tblCliente where IDCliente = " + MainActivity.sharedPref.getString("id", ""));
                    if(rs != null && rs.next())
                    {
                        ((EditText) findViewById(R.id.txtEmail)).setText(rs.getString("email"));
                        ((EditText) findViewById(R.id.txtEndereco)).setText(rs.getString("endereco"));
                        ((EditText) findViewById(R.id.txtSenha)).setText(rs.getString("senha"));
                    }
                }
            }
            catch (Exception e){}
            return null;
        }
    }

    class Atualiza extends AsyncTask<String,String, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao conexao = new ClasseConexao();
            try
            {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate("update tblCliente set email = '" + ((EditText) findViewById(R.id.txtEmail)).getText() + "', senha = '" + ((EditText) findViewById(R.id.txtSenha)).getText() + "', endereco = '" + ((EditText) findViewById(R.id.txtEndereco)).getText() + "' where IDCliente = " + MainActivity.sharedPref.getString("id", ""));
                }
            }
            catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "Informaçoes atualizadas com sucesso! ;)", Toast.LENGTH_LONG).show();
        }
    }
}
