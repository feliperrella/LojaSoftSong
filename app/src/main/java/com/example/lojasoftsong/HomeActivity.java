package com.example.lojasoftsong;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nex3z.notificationbadge.NotificationBadge;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_home);
        bmb = findViewById(R.id.bmb);
        indicator = findViewById(R.id.indicator);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setExitTransition(fade);
        badge = findViewById(R.id.badge);
        carrossel = findViewById(R.id.carrossel);
        badge.setNumber(3);
        badge.bringToFront();
        final Carro adapter = new Carro();
        new Produtos().execute();
        carrossel.setAdapter(adapter);
        indicator.setViewPager(carrossel);
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder().normalImageRes(picId[i]).listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            switch (index)
                            {
                                case 0:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(HomeActivity.this, SearchActivity.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());
                                        }}, 700);
                                    break;
                                case 1:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(HomeActivity.this, PerfilActivity.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());
                                        }}, 700);
                                    break;
                                case 2:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(HomeActivity.this, CarrinhoActivity.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());
                                        }}, 700);
                                    break;
                            }
                        }
                    });
            bmb.addBuilder(builder);
            bmb.bringToFront();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        badge.setNumber(CarrinhoActivity.Nome.size());
    }

    public class Carro extends PagerAdapter{
        @Override
        public int getCount() {
            return CarrosselID.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View views = inflater.inflate(R.layout.layout_carrossel, container, false);
            ImageView f = views.findViewById(R.id.carrossel_images);
            f.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(HomeActivity.this).load(CarrosselID[position]).into(f);
            container.addView(views);
            return views;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            (container).removeView((View) object);
        }




    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls, mCodigo, mPreco);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL)
        {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }

    class Produtos extends AsyncTask<String, String, String>
    {
        ClasseConexao conexao = new ClasseConexao();
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("Select IDProduto, nome, preco_unitario, caminho_imagem from tblProduto limit 30");
                    rs.beforeFirst();
                    while(rs.next())
                    {
                        mCodigo.add(rs.getString("IDProduto"));
                        mNames.add(rs.getString("nome"));
                        mPreco.add(rs.getString("preco_unitario"));
                        mImageUrls.add(rs.getString("caminho_imagem"));
                    }
                }
            }
            catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            initRecyclerView();
        }
    }


    CircleIndicator indicator;
    ViewPager carrossel;
    int[] CarrosselID = {R.drawable.carrossel1, R.drawable.carrossel2, R.drawable.carrossel3, R.drawable.carrossel4};
    int[] picId = {R.drawable.ic_search, R.drawable.ic_person,R.drawable.ic_cart,R.drawable.ic_about};
    BoomMenuButton bmb;
    private static final String TAG = "MainActivity";
    private static final int NUM_COLUMNS = 2;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mCodigo = new ArrayList<>();
    private ArrayList<String> mPreco = new ArrayList<>();
    NotificationBadge badge;
}