package com.example.kulubs.model;

public class Review {
    private String namaUser;
    private int jumlahUlasan;
    private String tanggalReview; // Ubah dari Date ke String
    private float rating;
    private String isiReview;
    private int jumlahLike;

    public Review(String namaUser, int jumlahUlasan, String tanggalReview, float rating, String isiReview, int jumlahLike) {
        this.namaUser = namaUser;
        this.jumlahUlasan = jumlahUlasan;
        this.tanggalReview = tanggalReview;
        this.rating = rating;
        this.isiReview = isiReview;
        this.jumlahLike = jumlahLike;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public int getJumlahUlasan() {
        return jumlahUlasan;
    }

    public String getTanggalReview() {
        return tanggalReview;
    }

    public float getRating() {
        return rating;
    }

    public String getIsiReview() {
        return isiReview;
    }

    public int getJumlahLike() {
        return jumlahLike;
    }
}
