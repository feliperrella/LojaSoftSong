package com.example.lojasoftsong;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SearchActivity  extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ((EditText) findViewById(R.id.txtSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    new Search().execute();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    class Search extends AsyncTask<String,String,String>
    {
        ClasseConexao classeConexao;
        @Override
        protected String doInBackground(String... strings) {
            classeConexao = new ClasseConexao();
            try {
                Connection connection = classeConexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    String x = "Select IDProduto, nome, preco_unitario, caminho_imagem from tblProduto where nome like '" + ((EditText) findViewById(R.id.txtSearch)).getText() + "%' or categoria like '" + ((EditText) findViewById(R.id.txtSearch)).getText() + "%'";
                    ResultSet rs = stmt.executeQuery("Select IDProduto, nome, preco_unitario, caminho_imagem from tblProduto where nome like '%" + ((EditText) findViewById(R.id.txtSearch)).getText() + "%'");
                    rs.beforeFirst();
                    while(rs.next())
                    {
                        mCodigo.add(rs.getString("IDProduto"));
                        mNames.add(rs.getString("nome"));
                        mPreco.add(rs.getString("preco_unitario"));
                        mImageUrls.add(rs.getString("caminho_imagem"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            initRecyclerView();
        }
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls, mCodigo, mPreco);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mCodigo = new ArrayList<>();
    private ArrayList<String> mPreco = new ArrayList<>();
}
