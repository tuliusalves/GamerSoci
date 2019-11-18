package com.example.gamersoci.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.gamersoci.R;
import com.example.gamersoci.activity.PerfilAmigoActivity;
import com.example.gamersoci.adapter.AdapterPesquisa;
import com.example.gamersoci.helper.ConfiguracaoFirebase;
import com.example.gamersoci.helper.RecyclerItemClickListener;
import com.example.gamersoci.helper.UsuarioFirebase;
import com.example.gamersoci.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    //Widget
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuarioRef;//referência para usuários
    private AdapterPesquisa adapterPesquisa;//Criando atributo para adapterPesquisa
    private String idUsuarioLogado;//id do usuario logado



    public PesquisaFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_pesquisa, container, false);
        searchViewPesquisa=view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa=view.findViewById(R.id.recyclerPesquisa);

        //configurações iniciais
        listaUsuarios= new ArrayList<>();//array de lista de usuários
        usuarioRef= ConfiguracaoFirebase.getFirebase()
                .child("usuarios");
        idUsuarioLogado= UsuarioFirebase.getIdentificadorUsuario();


        //Configurar RecyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));//configuração básica
        //Configurar evento de clique
        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //recuperar o Usuário que foi clicado
                        Usuario usuarioSelecionado= listaUsuarios.get(position);
                        //Direcionando o click para PerfilAmigoActivity
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                        /*LEMBRETE
                        * PARA PASSAR UM OBJETO DE UMA ACTIVITY PARA OUTRA PRECISAMOS IMPLEMENTAR
                        * UMA INTERFACE NO MODEL. NO NOSSO CASO FAZEMOS ISSO EM "USUARIO", UTILIZAMOS
                        * DO RECURSO "implements Serializable" */
                        i.putExtra("usuarioSelecionado",usuarioSelecionado);
                        startActivity(i);//Startando a Activity
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        adapterPesquisa= new AdapterPesquisa(listaUsuarios,getActivity());//Instanciando Adapter Pesquisa que passa
        // a lista de Usuários e o context, no caso getActivity
        recyclerPesquisa.setAdapter( adapterPesquisa);
        //configura searchView
        //ao apertar na lupa ele irá mostrar a mensagem Buscar usuários
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //Escrever e mostrar os nomes que o usuário digitar
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            //captura oque foi digitado
            public boolean onQueryTextChange(String newText) {
                String textoDigitado= newText.toUpperCase();//enviando para a pesquisa letras maiúsculas
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });
    return view;
    }
    private void pesquisarUsuarios(String texto){
        //fazendo uma lista para guardar usuários

        //Limpando a lista
        listaUsuarios.clear();
        //Pesquisar usuários quando tiver um texto na pesquisa
        if(texto.length()>=2){//somente fará pesquisa quando o usuário digitar dois ou três caracteres
        Query query= usuarioRef.orderByChild("nome")
                .startAt(texto)//começar com texto
                .endAt(texto + "\uf8ff");//uf8ff é para procurar entre uma letra e outra

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //ordem da pesquisa 1- limpa lista 2-Monta a lista 3- Notificar

                    //Limpando a lista aqui para caso de demora na notificação
                    listaUsuarios.clear();

                    //2-Monta a Lista
                    for(DataSnapshot ds: dataSnapshot.getChildren()){

                        //Remover usuário que já está logado, da lista de pesquisa
                        Usuario usuario= ds.getValue(Usuario.class);
                        if(idUsuarioLogado.equals(usuario.getId()))
                            continue;//O continue força a volta para o for sem executar o código abaixo

                        //recuperando usuário do firebase
                        listaUsuarios.add( usuario);

                    }
                    //3-Notifica
                    adapterPesquisa.notifyDataSetChanged();//notificar o adapter
                    //int total = listaUsuarios.size();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }
}
