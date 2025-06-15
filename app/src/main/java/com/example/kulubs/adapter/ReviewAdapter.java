package com.example.kulubs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kulubs.R;
import com.example.kulubs.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        String jumlahUlasanDanTanggal = review.getJumlahUlasan() + " ulasan | " + review.getTanggalReview();

        holder.namaUserTextView.setText(review.getNamaUser());
        holder.jumlahUlasanTextView.setText(jumlahUlasanDanTanggal);
        holder.isiReviewTextView.setText(review.getIsiReview());
        holder.jumlahLikeTextView.setText(String.valueOf(review.getJumlahLike()));

        // Tambah bintang
        holder.ratingStarsLayout.removeAllViews();
        int fullStars = (int) review.getRating();
        for (int i = 0; i < fullStars; i++) {
            ImageView star = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40); // ukuran bintang
            params.setMargins(4, 0, 4, 0); // margin antar bintang
            star.setLayoutParams(params);
            star.setImageResource(R.drawable.ic_star); // pastikan ini gambar bintang
            holder.ratingStarsLayout.addView(star);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String nama = reviewList.get(pos).getNamaUser();
                    Toast.makeText(context, "Review dari: " + nama, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView namaUserTextView;
        TextView jumlahUlasanTextView;
        TextView isiReviewTextView;
        TextView jumlahLikeTextView;
        LinearLayout ratingStarsLayout;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_review_card);
            namaUserTextView = itemView.findViewById(R.id.textName);
            jumlahUlasanTextView = itemView.findViewById(R.id.jumlahUlasanTextView);
            isiReviewTextView = itemView.findViewById(R.id.isiReviewTextView);
            jumlahLikeTextView = itemView.findViewById(R.id.jumlahLikeTextView);
            ratingStarsLayout = itemView.findViewById(R.id.ratingStars);
        }
    }
}
