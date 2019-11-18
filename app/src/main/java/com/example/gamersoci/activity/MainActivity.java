package com.example.gamersoci.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.gamersoci.R;
import com.example.gamersoci.fragment.FeedFragment;
import com.example.gamersoci.fragment.PerfilFragment;
import com.example.gamersoci.fragment.PesquisaFragment;
import com.example.gamersoci.fragment.PostagemFragment;
import com.example.gamersoci.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CONFIGURANDO TOOLBAR
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        setSupportActionBar(myToolbar);

        //OBJETO DE AUTENTICACAO
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //CONFIGURAÇÃO BOTTOM NAVIGATIION VIEW
        configuraBottomNavigationView();
        //FAZENDO O FEED SER CARREGADO COM A ACTIVITY
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();


    }
    /**
     * Método responsável por tratar eventos de click na BottomNavigation
     * @param viewEx
     */
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){
    //METODO SETA O EVENTO DE CLICK NO NAVEGATION
       viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               //SWITCH CASE PRA AÇOES AO APERTAR OS BOTOES
                //OBJETOS PARA CARREGAR OS FRAGMENTS
               FragmentManager fragmentManager = getSupportFragmentManager();
               FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

               switch (item.getItemId()){
                   case R.id.ic_home ://CARREGAMOS OS FRAGMENTS
                       fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();//O VIEW PAGE TA NO XML ELE VAI SER TROCADO PELO FRAGMENTO
                       return true;
                   case R.id.ic_pesquisa :
                       fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                       return true;
                   case R.id.ic_postagem :
                       fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                       return true;
                   case R.id.ic_perfil :
                       fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                       return true;
               }
                //CASO NAO SEJA CARREGADO NENHUM FRAGMENTO
               return false;
           }
       });


    }

    //METODO QUE CRIA BOTTOM NAVIGATION
    private void configuraBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

    //configurações visuais
        bottomNavigationViewEx.enableAnimation(true);//ANIMAÇÕES
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);

        //Habilitar navegação
        habilitarNavegacao( bottomNavigationViewEx );

        //configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }

    //CRIANDO MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);// O MENU É PASSADO AQUI

        return super.onCreateOptionsMenu(menu);

    }
    //TRATANDO DOS ITENS DO MENU QUANDO SELECIONADOS PELO USER
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario(); //DESLOGAMOS E MANDAMOS PARA A ACTIVITY LOGIN
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //DESLOGAR USUARIO
    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
