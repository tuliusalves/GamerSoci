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
import com.example.gamersoci.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //ATRIBUTOR
    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();
        inicializarComponentes();
        //LOGIN DO USUARIO
        progressBar.setVisibility(View.GONE);
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RECUPERANDO DADOS
                String textoEmail = campoEmail.getText().toString();
                String textosenha = campoSenha.getText().toString();
                if( !textoEmail.isEmpty() ){
                    if( !textosenha.isEmpty() ){
                        //PASSANDO INFORMAÇÕES PARA USUARIO OBJ
                        usuario = new Usuario();
                        usuario.setEmail( textoEmail );
                        usuario.setSenha( textosenha );
                        validarLogin( usuario ); //VALIDANDO LOGIN
                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Preencha o e-mail!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //VERIFICA SE O USUARIO ESTA LOGADO OU NAO AO ABRIR O APP
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if( autenticacao.getCurrentUser() != null ){//SE NAO LOGADO MANDA PRA MAN
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin( Usuario usuario ){
        progressBar.setVisibility( View.VISIBLE );
        //OBJ DE AUTENTICAÇÃO
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //METODO DE LOGAR
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {//VERIFICA SE FOI FEITO CORRETAMENTE A AUTH

                if ( task.isSuccessful() ){//CASO TENHA SUCESSO ENVIA USUARIO PARA TELA PRINCIPAL
                    progressBar.setVisibility( View.GONE );
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));//PASSANDO PARA ACTIVITY MAIN
                    finish();
                }else { //CASO NAO TENHA SUCESSO
                    Toast.makeText(LoginActivity.this,
                            "Login ou senha incorretos",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility( View.GONE );
                }

            }
        });


    }
        //EVENTO QUE LEVA ATE A TELA DE CADASTRO
    public void abrirCadastro(View view){
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity( i );
    }

    public void inicializarComponentes(){
        campoEmail   = findViewById(R.id.editLoginEmail);
        campoSenha   = findViewById(R.id.editLoginSenha);
        botaoEntrar  = findViewById(R.id.buttonLoginEntrar);
        progressBar  = findViewById(R.id.progressLogin);
        //aplicando foco no campo email
        campoEmail.requestFocus();
    }
}
