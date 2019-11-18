package com.example.gamersoci.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gamersoci.R;
import com.example.gamersoci.helper.ConfiguracaoFirebase;
import com.example.gamersoci.helper.UsuarioFirebase;
import com.example.gamersoci.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;

    private DatabaseReference fireBaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;//id do usuario logado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Configurações iniciais
        fireBaseRef=  ConfiguracaoFirebase.getFirebase();
        usuariosRef = fireBaseRef.child("usuarios");//voltado para acessar usuários no firebase
        seguidoresRef = fireBaseRef.child("seguidores");
        idUsuarioLogado= UsuarioFirebase.getIdentificadorUsuario();

        //Inicializar componentes
        inicializarComponentes();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Pefil");
        setSupportActionBar( toolbar );
        //BOTAO DE VOLTAR
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //SUBSTITUI O BOTAO VOLTAR POR UM DE SUA PREFERENCIA
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");//obs (Usuario) é uma conversão para o tipo usuário

            //Configura nome do usuário na toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome() );

            //Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if( caminhoFoto != null ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imagePerfil );
            }

        }

    }
    //recuperando os dados do usuário que está logado
    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef= usuariosRef.child(idUsuarioLogado);
        //fazer pesquisa apenas uma vez, para não continuar recuperando os dados
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //recuperar os dados do usuário logado
                    usuarioLogado= dataSnapshot.getValue(Usuario.class);
                    //Agora verificamos se o usuário já está seguindo o amigo selecionado
                        verificaSegueUsuarioAmigo();//Este método é dependente do dados do usuário logado
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguindoRef= seguidoresRef
                .child( idUsuarioLogado)
                .child( usuarioSelecionado.getId());//usuário selecionado mediante ao seu ID.
        seguindoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Verifica se existe dados no Snapshot, ou seja o caminho da foto
                        if( dataSnapshot.exists()){
                            //Usuário já está sendo seguido
                            //habilitar e desabilitar automaticamente o botão
                            habilitarBotaoSeguir( true );

                        } else{
                          //Ainda não está sendo seguido
                            habilitarBotaoSeguir(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
    //método para habilitar ou desabilitar o botão caso o Usuário já esteja ou não sendo seguido
    private void habilitarBotaoSeguir(boolean segueUsuario){
        if(segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        }else{
            buttonAcaoPerfil.setText("Seguir");
            //Oferecer a opção de adicionar o Usuário
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Salvar o seguidor
                    salvarSeguidor(usuarioLogado,usuarioSelecionado);
                }
            });
        }

    }
    //Método para salvar Seguidor
    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){
        /*ESTRUTURA
        1-SEGUIDORES 2-USUÁRIO QUE ESTÁ LOGADO 3- USUÁRIO QUE QUER SEGUIR 4-DADOS SEGUINDO
         */
        //Vamos fazer um HashMap apenas salvando o nome e o caminho da foto do usuário
        HashMap<String,Object> dadosAmigos= new HashMap<>();
        dadosAmigos.put("nome", uAmigo.getNome());
        dadosAmigos.put("caminhoFoto",uAmigo.getCaminhoFoto());
        //Porém devemos incrementar todos os dados usuário e de quem vamos seguir
        DatabaseReference seguidorRef=seguidoresRef
                .child(uLogado.getId())//2
                .child(uAmigo.getId());//3
        seguidorRef.setValue(dadosAmigos);//4

        //Alterar botão ação para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        //Isso serve para eliminar a ação do botão caso o usuário já esteja seguindo alguém
        buttonAcaoPerfil.setOnClickListener(null);//isso tira o evento de click

        //Incrementar seguindo do usuário logado
        int seguindo= uLogado.getSeguindo()+1;
        HashMap<String,Object> dadosSeguindo= new HashMap<>();
        dadosSeguindo.put("seguindo",seguindo);//passando o seguindo para o hashMap
        //Agora vamos salvar esses dados
        DatabaseReference usuarioSeguindo=  usuariosRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);//atualizar o nó

        //Incrementar seguidores do amigo
        int seguidores= uAmigo.getSeguindo()+1;/*Usamos o uAmigo porque aqui queremos saber quantas
        pessoas estão sendo seguidas pelo nosso amigo, logo isso é uma informação DELE! */
        HashMap<String,Object> dadosSeguidores= new HashMap<>();
        dadosSeguidores.put("seguidores",seguidores);//passando o seguindo para o hashMap
        //Agora vamos salvar esses dados
        DatabaseReference usuarioSeguidores=  usuariosRef
                .child(uAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);//atualizar o nó

    }
    //Adicionando e removendo o evento

    @Override
    protected void onStart() {
        super.onStart();
        //Recuperar dados do amigo Selecionado
        recuperarDadosPerfilAmigo();
        //Colocamos o método abaixo aqui, pq. enquanto o usuário está seguindo alguém, outra pessoa pode segui-lo
        //Recuperar Dados usuário Logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    //remover o listener quando não estivermos o utilizando
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener( valueEventListenerPerfilAmigo );
    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getId() );//Pegando o Id de um usuario amigo
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Usuario usuario = dataSnapshot.getValue( Usuario.class );//recuperar usuário

                        //configuração...
                        //1-Converte para string pois  originalmente eles são um int
                        String postagens = String.valueOf( usuario.getPostagens() );
                        String seguindo = String.valueOf( usuario.getSeguindo() );
                        String seguidores = String.valueOf( usuario.getSeguidores() );

                        //2-Configura valores recuperados
                        textPublicacoes.setText( postagens );
                        textSeguidores.setText( seguidores );
                        textSeguindo.setText( seguindo );

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void inicializarComponentes(){
        imagePerfil = findViewById(R.id.imagePerfil);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil.setText("Seguir");
    }
    //fechando a tela ao apertar o "x"
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
