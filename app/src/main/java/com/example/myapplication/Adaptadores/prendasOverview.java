package com.example.myapplication.Adaptadores;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ConexionBBDDLocal;
import com.example.myapplication.Entidades.Prenda;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class prendasOverview extends BaseAdapter implements View.OnClickListener {

    private List<Prenda> p;
    private Context c;
    private View.OnClickListener listener;
    private OnItemDeleteListener mDeleteListener;

    public prendasOverview(List<Prenda> lP, Context context){
        this.p=lP;
        this.c=context;
    }

    //interfaz pública en caso de que querramos eliminar una prenda de la lista
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    //si se pulsa el botón de X para eliminar una prenda
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        mDeleteListener = listener;
    }

    @Override
    public int getCount() {
        return p.size();
    }

    @Override
    public Object getItem(int pos) {
        return p.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;

        //se inicializa la vista
        if (view==null){
            LayoutInflater inflater = LayoutInflater.from(this.c);
            view = inflater.inflate(R.layout.lista_items, viewGroup, false);
            //se identifican los objetos del layout lista_items
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.imageView);
            holder.tituloTextView = view.findViewById(R.id.tituloTextView);
            holder.descripcionTextView = view.findViewById(R.id.descripcionTextView);
            holder.fechaTextView = view.findViewById(R.id.fechaTextView);
            holder.btn_eliminar = view.findViewById(R.id.button_eliminar);
            view.setTag(holder);

        } else{
            holder = (ViewHolder) view.getTag();
        }
        // se rellenan los campos de la vista de las prendas con lo que se obtiene de los objetos Prenda
        Prenda prenda = p.get(pos);
        // Debería haber sido el de la siguiente manera
        //      para que se introdujera la imagen deseada por cada prenda
        //  holder.imageView.setImageBitmap(prenda.getFoto());
        holder.imageView.setImageResource(prenda.getFoto());
        holder.tituloTextView.setText(prenda.getTituloPrenda());
        holder.descripcionTextView.setText(prenda.getDescripcion());
        holder.fechaTextView.setText(prenda.getFechaColgado());
        holder.btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si se pulsa el botón de eliminar una prenda en concreto
                if (mDeleteListener != null) {
                    mDeleteListener.onItemDelete(pos);
                }

            }
        });
        return view;
    }

    public void setOnClickListener(View.OnClickListener pListener) {
        this.listener = pListener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    //se definen los atributos que necesitamos en la vista
    private static class ViewHolder{
        ImageView imageView;
        TextView tituloTextView;
        TextView descripcionTextView;
        TextView fechaTextView;
        Button btn_eliminar;
    }

}
