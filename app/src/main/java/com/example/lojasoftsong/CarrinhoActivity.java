package com.example.lojasoftsong;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.gelitenight.waveview.library.WaveView;

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
        WaveView waveView = findViewById(R.id.waver);
        WaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setWaveColor(
                Color.parseColor("#B079E1"),
                Color.parseColor("#285FBAE6"));
        ((TextView) findViewById(R.id.nCarrinho)).setText(Nome.size() + "");
        if (Nome.size() == 0)
        {
            (findViewById(R.id.ButtonConfirma)).setEnabled(false);
            (findViewById(R.id.ButtonConfirma)).setBackgroundColor(Color.GRAY);
        }
        (findViewById(R.id.ButtonConfirma)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(CarrinhoActivity.this, ConfirmActivity.class);
                startActivity(x, ActivityOptions.makeSceneTransitionAnimation(CarrinhoActivity.this).toBundle());
            }
        });
        adaptador Adaptador= new adaptador();
        ((ListView) findViewById(R.id.lista)).setAdapter(Adaptador);
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
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_produtos_linha, null);
            final ImageView pic = convertView.findViewById(R.id.ProdPic);
            final TextView name = convertView.findViewById(R.id.ProdName);
            final TextView price = convertView.findViewById(R.id.ProdVal);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Nome.remove(name.getText().toString());
                    Preco.remove(price.getText().toString());
                    adaptador Adaptador= new adaptador();
                    ((ListView) findViewById(R.id.lista)).setAdapter(Adaptador);
                }
            });
            Thread x = new Thread(){
                @Override
                public void run() {
                    super.run();
                    Glide.with(getApplicationContext()).load(R.drawable.teste).into(pic);
                    name.setText(Nome.get(position));
                    price.setText(Preco.get(position));
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
        ((TextView) findViewById(R.id.nCarrinho)).setText(Nome.size() + "");
        if (Nome.size() == 0)
        {
            (findViewById(R.id.ButtonConfirma)).setEnabled(false);
            (findViewById(R.id.ButtonConfirma)).setBackgroundColor(Color.GRAY);
        }
    }
    private WaveHelper WaveHelper;
    public static ArrayList<String> Nome = new ArrayList<>(), Preco = new ArrayList<>(), Caminho_Imagem = new ArrayList<>();
}
