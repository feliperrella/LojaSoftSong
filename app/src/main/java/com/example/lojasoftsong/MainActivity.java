package com.example.lojasoftsong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setExitTransition(fade);
        setContentView(R.layout.activity_main);

        sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        if(!sharedPref.getString("email", "").equals(""))
        {
            Intent my = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(my);
            finish();
        }
        Login = findViewById(R.id.entrar);
        progressBar = findViewById(R.id.progress_bar);
        usu = findViewById(R.id.txtEmail);
        senh = findViewById(R.id.txtSenha);
        cadastrar = findViewById(R.id.btnCadastro);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(MainActivity.this, SigninActivity.class);
                View sharedButton = findViewById(R.id.btnCadastro);
                View sharedImage = findViewById(R.id.logo);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedImage, "logo");
                startActivity(x, activityOptions.toBundle());
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new loga().execute();
                Login.animate().alpha(0f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                progressBar.setAlpha(1f);
                                progressBar
                                        .getIndeterminateDrawable()
                                        .setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                                progressBar.setVisibility(VISIBLE);
                            }
                        })
                        .start();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class loga extends AsyncTask<String, String, String>
    {
        ResultSet rs;
        ClasseConexao conexao = new ClasseConexao();

        @Override
        protected String doInBackground(String... strings) {
            try {
                conexao = new ClasseConexao();
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    //String query = "Select * from tblUsuario where email = '" + usu.getText().toString() + "' and senha = '" + senh.getText().toString() + "'";
                    rs = stmt.executeQuery("Select * from tblCliente where email = '" + usu.getText().toString() + "' and senha = '" + senh.getText().toString() + "'");
                    if(rs != null && rs.next())
                    {
                        id = rs.getString("IDCliente");
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
            try {
                if(id != "")
                {
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    Toast.makeText(MainActivity.this, "Login feito com sucesso", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("email" ,usu.getText().toString());
                    editor.putString("id", id);
                    editor.apply();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Usuario ou senha Incorreto :(", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBar.animate().alpha(0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Login.setAlpha(1f);
                            Login.setVisibility(VISIBLE);
                        }
                    })
                    .start();
        }
    }

    public static SharedPreferences sharedPref;
    Button cadastrar;
    EditText usu, senh;
    String id = "";
    ImageView Login;
    ProgressBar progressBar;
}
