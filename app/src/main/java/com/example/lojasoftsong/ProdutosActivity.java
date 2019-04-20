package com.example.lojasoftsong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import cdflynn.android.library.checkview.CheckView;

public class ProdutosActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_produtos);
        new Load().execute();
        gyroscopeObserver = new GyroscopeObserver();
        gyroscopeObserver.setMaxRotateRadian(Math.PI/9);
        PanoramaImageView panoramaImageView = findViewById(R.id.panorama_image_view);
        add = findViewById(R.id.btnAdd);
        tchau = findViewById(R.id.tchau);
        check = findViewById(R.id.checkk);
        panoramaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inf = (LayoutInflater)  getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inf.inflate(R.layout.layout_pic, null);
                Glide.with(getApplicationContext()).load(R.drawable.teste).into((PhotoView) v.findViewById(R.id.pic));
                AlertDialog.Builder alert = new AlertDialog.Builder(ProdutosActivity.this);
                alert.setView(v);
                alert.create();
                alert.show();
            }
        });
        PrecoPromo = findViewById(R.id.txtPromo);
        PrecoPromo.setPaintFlags(PrecoPromo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver);
        (findViewById(R.id.btnAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                width();
                resto();
                HomeActivity.Carrinho.add(StaggeredRecyclerViewAdapter.id);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Register GyroscopeObserver.
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister();
    }

    class Load extends AsyncTask<String,String,String>
    {
        ClasseConexao conexao = new ClasseConexao();
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    String query = "select nome, preco_unitario, caminho_imagem, descricao,(select desconto from tblPromocao where ID_Produto =" + StaggeredRecyclerViewAdapter.id + ") as promo from tblProduto where IDProduto = " + StaggeredRecyclerViewAdapter.id;
                    final ResultSet rs = stmt.executeQuery(query);
                    if(rs != null && rs.next())
                    {
                        a = rs.getString("nome");
                        b = rs.getString("promo") == null ? rs.getString("preco_unitario") : rs.getString("promo");
                        c = rs.getString("promo") == null ? "" : rs.getString("preco_unitario");
                        b = b.substring(0, b.length() - 2);
                        c = c.substring(0, c.length()-2);
                        d = rs.getString("descricao");
                    }
                }
            }
            catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((TextView) findViewById(R.id.txtTitulo)).setText(a);
            ((TextView) findViewById(R.id.txtPreco)).setText(b);
            ((TextView) findViewById(R.id.txtPromo)).setText(c);
            ((TextView) findViewById(R.id.txtDesc)).setText(d);
        }
    }

    public void width()
    {
        ValueAnimator anim = ValueAnimator.ofInt(add.getMeasuredWidth(), 110);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = add.getLayoutParams();
                layoutParams.width = val;
                add.requestLayout();
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    public void resto()
    {
        tchau.animate().alpha(0f)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        check.check();
                    }
                })
                .start();
    }
    CardView add;
    private GyroscopeObserver gyroscopeObserver;
    TextView tchau, PrecoPromo;
    CheckView check;
    String a = null, b = null, c = null, d = null;
    private PhotoViewAttacher mAttacher;
}
