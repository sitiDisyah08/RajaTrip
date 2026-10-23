package com.example.rajatrip;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OpenTripActivity extends Activity {

    private LinearLayout tripList;
    private EditText etSearchTrip;

    private String activeFilter = "Semua";
    private final List<Trip> trips = new ArrayList<>();

    private static class Trip {
        String type;
        String title;
        String price;
        String duration;
        String rating;
        String facilities;
        int image;

        Trip(String type, String title, String price,
             String duration, String rating,
             String facilities, int image) {
            this.type = type;
            this.title = title;
            this.price = price;
            this.duration = duration;
            this.rating = rating;
            this.facilities = facilities;
            this.image = image;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_trip);

        tripList = findViewById(R.id.tripList);
        etSearchTrip = findViewById(R.id.etSearchTrip);

        loadTrips();
        showTrips();

        findViewById(R.id.btnBackOpenTrip).setOnClickListener(v ->
                finish()
        );

        setupFilters();
        setupSearch();

        findViewById(R.id.navHomeOpenTrip).setOnClickListener(v ->
                finish()
        );

        findViewById(R.id.navPaketOpenTrip).setOnClickListener(v ->
                Toast.makeText(this, "Kamu berada di halaman Open Trip",
                        Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.navDestinasiOpenTrip).setOnClickListener(v ->
                Toast.makeText(this, "Halaman Destinasi belum dihubungkan",
                        Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.navPesananOpenTrip).setOnClickListener(v ->
                Toast.makeText(this, "Halaman Pesanan belum dihubungkan",
                        Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.navProfilOpenTrip).setOnClickListener(v ->
                Toast.makeText(this, "Halaman Profil belum dihubungkan",
                        Toast.LENGTH_SHORT).show()
        );
    }

    private void loadTrips() {
        trips.clear();

        trips.add(new Trip(
                "3D2N",
                "Open Trip Raja Ampat 3D2N",
                "Rp3.500.000",
                "3 Hari • 2 Malam",
                "4.9 (1.2k ulasan)",
                "▣ 3 Hari   •   ⚓ Kapal Wisata   •   ♨ Makan 3x Sehari   •   ♧ Snorkeling & Diving",
                R.drawable.gambar_trip_3d2n
        ));

        trips.add(new Trip(
                "4D3N",
                "Open Trip Raja Ampat 4D3N",
                "Rp4.800.000",
                "4 Hari • 3 Malam",
                "4.8 (856 ulasan)",
                "▣ 4 Hari   •   ⚓ Kapal Wisata   •   ♨ Makan 3x Sehari   •   ♦ Destinasi Lengkap",
                R.drawable.raja_ampat3
        ));

        trips.add(new Trip(
                "5D4N",
                "Open Trip Misool 5D4N",
                "Rp6.500.000",
                "5 Hari • 4 Malam",
                "4.7 (642 ulasan)",
                "▣ 5 Hari   •   ⚓ Kapal Wisata   •   ♨ Makan 3x Sehari   •   ♧ Snorkeling & Diving",
                R.drawable.raja_ampat2
        ));

        trips.add(new Trip(
                "Private Trip",
                "Private Trip Raja Ampat",
                "Rp7.500.000",
                "Paket privat",
                "5.0 (Paket contoh)",
                "▣ Jadwal fleksibel    ⛴ Kapal privat    ◉ Destinasi pilihan",
                R.drawable.raja_ampat
        ));
    }

    private void showTrips() {
        tripList.removeAllViews();

        String keyword = etSearchTrip.getText()
                .toString().trim().toLowerCase(Locale.ROOT);

        LayoutInflater inflater = LayoutInflater.from(this);
        int count = 0;

        for (Trip trip : trips) {
            boolean matchesFilter =
                    activeFilter.equals("Semua")
                            || activeFilter.equals(trip.type);

            boolean matchesSearch =
                    trip.title.toLowerCase(Locale.ROOT).contains(keyword)
                            || trip.type.toLowerCase(Locale.ROOT).contains(keyword)
                            || "raja ampat papua barat".contains(keyword);

            if (!matchesFilter || !matchesSearch) {
                continue;
            }

            View card = inflater.inflate(
                    R.layout.item_open_trip,
                    tripList,
                    false
            );

            ImageView image = card.findViewById(R.id.imgTrip);
            TextView title = card.findViewById(R.id.tvTripTitle);
            TextView location = card.findViewById(R.id.tvTripLocation);
            TextView rating = card.findViewById(R.id.tvTripRating);
            TextView price = card.findViewById(R.id.tvTripPrice);
            TextView duration = card.findViewById(R.id.tvTripDuration);
            TextView facilities = card.findViewById(R.id.tvTripFacilities);

            image.setImageResource(trip.image);
            title.setText(trip.title);
            location.setText("⌖ Raja Ampat, Papua Barat");
            rating.setText(trip.rating);
            price.setText(trip.price);
            duration.setText("/ orang • " + trip.duration);
            facilities.setText(trip.facilities);

            card.setOnClickListener(v -> openTripDetail(trip));

            tripList.addView(card);
            count++;
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("Paket tidak ditemukan.");
            empty.setTextColor(0xFF52616B);
            empty.setTextSize(14);
            empty.setPadding(16, 24, 16, 24);
            tripList.addView(empty);
        }
    }

    private void setupFilters() {
        setFilterClick(R.id.filterSemua, "Semua");
        setFilterClick(R.id.filter3D2N, "3D2N");
        setFilterClick(R.id.filter4D3N, "4D3N");
        setFilterClick(R.id.filter5D4N, "5D4N");
        setFilterClick(R.id.filterPrivate, "Private Trip");
    }

    private void setFilterClick(int viewId, String filter) {
        TextView filterView = findViewById(viewId);

        filterView.setOnClickListener(v -> {
            activeFilter = filter;
            updateFilterStyles();
            showTrips();
        });
    }

    private void updateFilterStyles() {
        int[] ids = {
                R.id.filterSemua,
                R.id.filter3D2N,
                R.id.filter4D3N,
                R.id.filter5D4N,
                R.id.filterPrivate
        };

        String[] names = {
                "Semua", "3D2N", "4D3N", "5D4N", "Private Trip"
        };

        for (int i = 0; i < ids.length; i++) {
            TextView view = findViewById(ids[i]);
            boolean selected = names[i].equals(activeFilter);

            view.setBackgroundResource(
                    selected
                            ? R.drawable.bg_filter_active
                            : R.drawable.bg_filter_inactive
            );

            view.setTextColor(
                    selected ? 0xFFFFFFFF : 0xFF17212B
            );
        }
    }

    private void setupSearch() {
        etSearchTrip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                showTrips();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void openTripDetail(Trip trip) {
        Toast.makeText(
                this,
                "Paket dipilih: " + trip.title
                        + "\nHalaman detail akan dibuat berikutnya.",
                Toast.LENGTH_LONG
        ).show();
    }
}
