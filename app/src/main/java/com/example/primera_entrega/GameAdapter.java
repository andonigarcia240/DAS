package com.example.primera_entrega;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList;
    private Context context;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(Game game);
        void onGameLongClick(Game game);
    }

    public GameAdapter(Context context, List<Game> gameList, OnGameClickListener listener) {
        this.context = context;
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);

        holder.tvNombre.setText(game.getNombre());
        holder.tvPlataforma.setText(game.getPlataforma());
        holder.tvEstado.setText(game.getEstado());
        holder.tvHoras.setText(game.getHorasJugadas() + "h jugadas");
        holder.tvPuntuacion.setText("⭐ " + game.getPuntuacion() + "/10");
        holder.tvFecha.setText("Última sesión: " + game.getFechaUltimaSesion());

        // Color del estado
        int colorRes;
        switch (game.getEstado()) {
            case "Completado":
                colorRes = context.getResources().getColor(R.color.estado_completado, null);
                break;
            case "Abandonado":
                colorRes = context.getResources().getColor(R.color.estado_abandonado, null);
                break;
            default: // Jugando
                colorRes = context.getResources().getColor(R.color.estado_jugando, null);
                break;
        }
        holder.tvEstado.setBackgroundColor(colorRes);

        holder.cardView.setOnClickListener(v -> listener.onGameClick(game));
        holder.cardView.setOnLongClickListener(v -> {
            listener.onGameLongClick(game);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void updateList(List<Game> newList) {
        this.gameList = newList;
        notifyDataSetChanged();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvNombre, tvPlataforma, tvEstado, tvHoras, tvPuntuacion, tvFecha;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPlataforma = itemView.findViewById(R.id.tvPlataforma);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvHoras = itemView.findViewById(R.id.tvHoras);
            tvPuntuacion = itemView.findViewById(R.id.tvPuntuacion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}