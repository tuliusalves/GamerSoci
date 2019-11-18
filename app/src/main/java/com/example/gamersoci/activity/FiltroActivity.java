package com.example.gamersoci.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.gamersoci.R;

public class FiltroActivity extends AppCompatActivity {

        private ImageView imageFotoEscolhida;
        private Bitmap imagem;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_filtro);

            //Inicializar componentes
            imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);

            //CONFIGURANDO TOOLBAR
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
            myToolbar.setTitle("Filtros");
            setSupportActionBar(myToolbar);
            //BOTAO DE VOLTAR
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //SUBSTITUI O BOTAO VOLTAR POR UM DE SUA PREFERENCIA
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

            //Recupera a imagem escolhida pelo usuário
            Bundle bundle = getIntent().getExtras();
            if( bundle != null ){
                byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
                //aqui devemos passar três parâmetros: 1- O byteArray
                // 2-de onde nós Inicíaremos a conversão para o bitmap
                // 3-até onde faremos a conversão, ou seja o tamanho do byteArray
                imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length );
                imageFotoEscolhida.setImageBitmap( imagem );
            }

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            //criando menu
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.ic_salvar_postagem:
                    break;
            }
        return super.onOptionsItemSelected(item);
    }

    //encerrando a Activity
    @Override
    public boolean onSupportNavigateUp() {
            finish();
        return false;
    }
}
