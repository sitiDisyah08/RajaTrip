package com.example.rajatrip;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menu Open Trip: membuka halaman OpenTripActivity
        View menuOpenTrip = findViewById(R.id.menuOpenTrip);

        if (menuOpenTrip != null) {
            menuOpenTrip.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        OpenTripActivity.class
                );
                startActivity(intent);
            });
        }

        // Menu pintasan lainnya
        setClick(R.id.menuDestinasi, "Halaman Destinasi");
        setClick(R.id.menuJadwal, "Jadwal perjalanan");
        setClick(R.id.menuPeta, "Peta Wisata");

        // Navigasi bawah
        setClick(R.id.navHome, "Kamu berada di Beranda");
        setClick(R.id.navDestinasi, "Halaman Destinasi");

        // Navigasi Paket Trip juga membuka halaman Open Trip
        View navPaketTrip = findViewById(R.id.navPaketTrip);

        if (navPaketTrip != null) {
            navPaketTrip.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        OpenTripActivity.class
                );
                startActivity(intent);
            });
        }

        setClick(R.id.navPesanan, "Halaman Pesanan");
        setClick(R.id.navProfil, "Halaman Profil");

        // Tombol Lihat Semua paket
        setOpenTripClick(R.id.btnSeeAllTrips);

        // Tombol promo
        setOpenTripClick(R.id.btnPromo);
    }

    // Fungsi untuk menu yang masih berupa placeholder
    private void setClick(int id, String message) {
        View view = findViewById(id);

        if (view != null) {
            view.setOnClickListener(v ->
                    Toast.makeText(
                            MainActivity.this,
                            message,
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }
    }

    // Fungsi untuk membuka halaman Open Trip
    private void setOpenTripClick(int id) {
        View view = findViewById(id);

        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        OpenTripActivity.class
                );
                startActivity(intent);
            });
        }
    }
}