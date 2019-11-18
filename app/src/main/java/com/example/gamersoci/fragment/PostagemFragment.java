package com.example.gamersoci.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gamersoci.R;
import com.example.gamersoci.activity.FiltroActivity;
import com.example.gamersoci.helper.Permissao;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {


    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA =100;
    private static final int SELECAO_GALERIA =200;
    //Array de permissões
    private String [] permissoesNecessarias= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostagemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_postagem, container, false);
        //Validando Permissões
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);
        //Inicializando componentes
        buttonAbrirGaleria= view.findViewById(R.id.buttonAbrirGaleria);
        buttonAbrirCamera= view.findViewById(R.id.buttonAbrirCamera);

        //configurando os eventos de clicque nos botões

        //Adicionando evento de clique no botão de câmera
        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });
        //Adicionando o evento de clique no botão Galeria
        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Action_Pick escolhe uma foto, daí escolhemos no externo que seria a galeria do celular
                Intent i= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            //criando a imagem
            Bitmap imagem= null;
            try{
                //Validar o tipo de seleção de imagem
                switch( requestCode){
                    case SELECAO_CAMERA :
                        //precisamos usar o (Bitmap) pois por padrdão getExtras não é desse tipo
                        imagem= (Bitmap)data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA :
                        Uri localImagensSelecionada= data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagensSelecionada);
                        break;
                }
                // a intenção aqui é mandar a imagem para uma tela de filtros
                //Valida imagem selecionada
                if(imagem != null){

                    //converter imagem em byte array
                    //Recuperar dados da imagem para o firebase
                    //ṔASSANDO IMAGEM PARA ARRAY
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //COMPRIMINDO
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    //ARRAY DE BYTES
                    byte[] dadosImagem = baos.toByteArray();

                    //Envia a imagem escolhida para aplicação de filtro
                    Intent i = new Intent(getActivity(), FiltroActivity.class);//direcionar para a filtroActivity
                    i.putExtra("fotoEscolhida",dadosImagem);
                    startActivity(i);//iniciar
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
