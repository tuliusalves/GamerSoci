package com.example.gamersoci.model;

import com.example.gamersoci.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    //ATRIBUTOS
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String caminhoFoto;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagens = 0;

    //CONSTRUTOR
    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child( getId() );
        usuariosRef.setValue( this ); //AQUI SE SALVA OS DADOS DO USUARIO
    }


    //ATUALIZAR DADOS
    public void atualizar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child(getId());

       Map<String, Object> valoresUsuario = converterParaMap();
       usuariosRef.updateChildren(valoresUsuario);
       //objeto.put("/usuarios/" + getId() + "/nome", getNome() );
        //objeto.put("/usuarios/" + getId() + "/caminhoFoto", getCaminhoFoto() );

        //firebaseRef.updateChildren( objeto );

    }

    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail() );
        usuarioMap.put("nome", getNome() );
        usuarioMap.put("id", getId() );
        usuarioMap.put("caminhoFoto", getCaminhoFoto() );
        usuarioMap.put("seguidores", getSeguidores() );
        usuarioMap.put("seguindo", getSeguindo() );
        usuarioMap.put("postagens", getPostagens() );

        return usuarioMap;

    }

    //GETTER AND SETTER

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getNome() {
        return nome;
    }

    public void setNome(String nome) {

        this.nome = nome.toUpperCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

@Exclude
    public String getSenha() { //A SENHA VAI FICAR SALVA SO EM ALTENTICAÇÃO
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
