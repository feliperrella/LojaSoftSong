package com.example.lojasoftsong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;

import cdflynn.android.library.checkview.CheckView;

import static android.view.View.VISIBLE;

public class SigninActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(fade);
        setContentView(R.layout.activity_signin);
        Cadastrar = findViewById(R.id.btnCadastro2);
        check = findViewById(R.id.check);
        tchau = findViewById(R.id.txtTchau);
        pb = findViewById(R.id.progress_bar1);
        Nome = findViewById(R.id.txtNome);
        Email = findViewById(R.id.txtEmail);
        Senha = findViewById(R.id.txtSenha);
        Endereço = findViewById(R.id.txtEndereco);
        Cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Nome.getText().toString().equals("") && !Email.getText().toString().equals("") && !Senha.getText().toString().equals("") && !Endereço.getText().toString().equals("")) {
                    width();
                    resto();
                    new Cadastrar().execute();
                }
                else
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void width()
    {
        ValueAnimator anim = ValueAnimator.ofInt(Cadastrar.getMeasuredWidth(), 110);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = Cadastrar.getLayoutParams();
                layoutParams.width = val;
                Cadastrar.requestLayout();
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
                        pb.setAlpha(1f);
                        pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                        pb.setVisibility(VISIBLE);
                    }
                })
                .start();
    }

    class Cadastrar extends AsyncTask<String,String,String>
    {
        ClasseConexao conexao = new ClasseConexao();
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                   Statement stmt = connection.createStatement();
                   count = stmt.executeUpdate("Insert into tblCliente(nivel_acesso, nome, tel1,email,senha,CPF, endereco, cidade, estado, CEP, genero) values(0,'" + Nome.getText().toString() + "','','" + Email.getText().toString() + "','" + Senha.getText().toString() + "', '', '" + Endereço.getText() + "', '', '', '','')");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(count != 0)
            {

                pb.animate().alpha(0f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                check.check();
                            }
                        })
                        .start();
                SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
                editor.putString("email", Email.getText().toString());
                editor.putString("endereco", Endereço.getText().toString());
                editor.putString("nome", Nome.getText().toString());
                editor.apply();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent Home = new Intent(SigninActivity.this, HomeActivity.class);
                        startActivity(Home, ActivityOptions.makeSceneTransitionAnimation(SigninActivity.this).toBundle());
                    }
                }, 1400);
            }
        }
    }
    CheckView check;
    int count;
    TextView tchau;
    ProgressBar pb;
    CardView Cadastrar;
    EditText Nome, Email, Senha, Endereço;
}
