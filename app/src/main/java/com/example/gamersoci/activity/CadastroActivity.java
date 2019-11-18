package com.example.gamersoci.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gamersoci.R;
import com.example.gamersoci.helper.ConfiguracaoFirebase;
import com.example.gamersoci.helper.UsuarioFirebase;
import com.example.gamersoci.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    //CAMPOS
    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;
    private Usuario usuario;

    //OBJETO DE AUTENTICAÇÃO FIREBASE
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //CADASTRAR USUARIO
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            //PASSANDO PARA OS CAMPOS
            public void onClick(View v) {
                String textoNome  = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textosenha = campoSenha.getText().toString();
             //VALIDANDO CAMPOS
                if( !textoNome.isEmpty() ){
                    if( !textoEmail.isEmpty() ){
                        if( !textosenha.isEmpty() ){
                            //CADASTRANDO USUARIO ATRAVES DO OBJ USUARIO DA CLASSE USUARIO
                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textosenha );
                            cadastrar( usuario ); //CHAMA METODO DE CADASTRO
                        }else{ //EXIBINDO MENSAGENS DE ALERTA
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o email!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * Método QUE cadastra usuário com e-mail e senha
     * e faz validações No cadastro / MANDA PRO FIREBASE
     */
    public void cadastrar(final Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //CRIANDO USUARIO COM EMAIL E SENHA
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener( //TRATANDO DOS ERROS
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful() ){ //SE EXECUTOU COM SUCESSO
                            try {
                                progressBar.setVisibility(View.GONE);
                                //Salvar dados no firebase
                                //GETRESULT PEGA INFORMAÇÕES DO USUARIO
                                //GETUSER NOS DEIXA ACESSAR OS DADOS DO USUARIO
                                //ELE PEGA O ID PRA SALVAR NO BD
                               String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId( idUsuario );
                                usuario.salvar();//METODO DA CLASSE USUARIO

                                //Salvar Nome no profile do Firebase
                                UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                                Toast.makeText(CadastroActivity.this,
                                        "Cadastro com sucesso",
                                        Toast.LENGTH_SHORT).show();
                                    //MANDA USUARIO PRA ACTIVITY MAN APOS CADASTRO COM SUCESSO
                                startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            progressBar.setVisibility( View.GONE );//SE DEU ERRO PARA PROGRESSBAR
                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Por favor, digite um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Este conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }
                            //EXIBINDO ERRO NA TELA
                            Toast.makeText(CadastroActivity.this,
                                    "Erro: " + erroExcecao ,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

    }
    // METODOS INICIALIZANDO COMPONENTES
    public void inicializarComponentes(){
        campoNome       = findViewById(R.id.editCadastroNome);
        campoEmail      = findViewById(R.id.editCadastroEmail);
        campoSenha      = findViewById(R.id.editCadastroSenha);
        botaoCadastrar  = findViewById(R.id.buttonCadastrar);
        progressBar     = findViewById(R.id.progressCadastro);
        //aplicando foco no campo email
        campoNome.requestFocus();
    }
}
