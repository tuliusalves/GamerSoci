package com.example.gamersoci.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamersoci.R;
import com.example.gamersoci.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder > {
    private List<Usuario> listUsuario;
    private Context context;

    //Construtor de AdapterPesquisa
    public AdapterPesquisa(List<Usuario> l, Context c) {
        this.listUsuario = l;
        this.context = c;
    }

    //Método de AdapterPesquisa
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //configurando a view, para apontar para adapter_pesquisa_usuario
        View itemLista= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pesquisa_usuario,parent,false);
        return new MyViewHolder(itemLista);//passando itemLista pra o MyViewHolder
    }
    //Método de AdapterPesquisa
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Aqui iremos colocar os items a serem exibidos

        Usuario usuario= listUsuario.get(position);//pegar os usuarios do firebase de acordo com a posição

        holder.nome.setText(usuario.getNome());//recupera o nome do usuário

        //testando se o Usuário tem a imagem
        if(usuario.getCaminhoFoto() !=null){
        //passando o caminho da foto
        Uri uri= Uri.parse(usuario.getCaminhoFoto());//convertendo para String
            Glide.with(context).load(uri).into(holder.foto);
        }else {//Caso o usuário não tenha foto, iremos passar a foto padrão avatar
            holder.foto.setImageResource(R.drawable.avatar);
        }

    }
    //Método de AdapterPesquisa
    @Override
    public int getItemCount() {
        return listUsuario.size();//passando a lista de acordo com o tamanho
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //Queremos exibir o nome e a foto
        CircleImageView foto;
        TextView nome;

        //Construtor do MyViewHolder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //Aqui passamos a foto e o nome para o MyviewHolder
            foto= itemView.findViewById(R.id.imageFotoPesquisa);
            nome= itemView.findViewById(R.id.textNomePesquisa);
        }
    }
}
