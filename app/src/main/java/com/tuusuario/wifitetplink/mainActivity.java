package com.tuusuario.wifitetplink;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar scanProgress;
    private RecyclerView networksRecyclerView;
    private Button btnScan;
    private NetworkAdapter adapter;
    private List<NetworkModel> networkList;
    private boolean isScanning = false;
    private Process suProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanProgress = findViewById(R.id.scanProgress);
        networksRecyclerView = findViewById(R.id.networksRecyclerView);
        btnScan = findViewById(R.id.btnScan);

        // Configurar la lista visual (RecyclerView)
        networkList = new ArrayList<>();
        networksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Acción cuando el usuario toca una red TP-Link específica de la lista
        adapter = new NetworkAdapter(networkList, new NetworkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NetworkModel network) {
                Toast.makeText(MainActivity.this, 
                        "Iniciando ataque contra: " + network.getSsid(), 
                        Toast.LENGTH_LONG).show();
                // Aquí se llamará en el futuro al método de ataque automatizado (Pixie-Dust, Handshake)
            }
        });
        networksRecyclerView.setAdapter(adapter);

        // Control del botón principal de escaneo
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScanning) {
                    startScan();
                } else {
                    stopScan();
                }
            }
        });
    }

    private void startScan() {
        isScanning = true;
        btnScan.setText("DETENER ESCANEO");
        btnScan.setBackgroundColor(0xFFFF0000); // Cambia el botón a Rojo
        scanProgress.setVisibility(View.VISIBLE); // Muestra la barra de carga animada
        networkList.clear();
        adapter.notifyDataSetChanged();

        // Hilo secundario para interactuar con los comandos del sistema sin congelar la pantalla
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Abrimos la tubería de superusuario
                    suProcess = Runtime.getRuntime().exec("su");
                    DataOutputStream outputStream = new DataOutputStream(suProcess.getOutputStream());
                    
                    // Comandos en segundo plano (Simulación de salida hacia el motor de la app)
                    outputStream.writeBytes("sleep 1\n"); // Simula tiempo de inicialización de la antena
                    outputStream.flush();

                    // Simulamos que el script va encontrando routers TP-Link en la consola raíz
                    // En una app final con Root real, aquí leerías de forma asíncrona un archivo CSV generado por airodump-ng
                    agregarRedDesdeConsola("TP-Link_Router_Home", "94:A6:7E:BC:11:22", "6", "WPS Pixie-Dust ✅");
                    Thread.sleep(1500); // Pausa simulada entre descubrimientos
                    
                    agregarRedDesdeConsola("TP-Link_Archer_C7", "50:3E:AA:88:99:00", "11", "PIN por defecto matemático 🔢");
                    Thread.sleep(1500);
                    
                    agregarRedDesdeConsola("TP-Link_Guest_Zone", "94:A6:7E:11:AA:BB", "1", "Captura de Handshake/PMKID 📡");

                } catch (Exception e) {
                    mostrarErrorEnUI();
                }
            }
        }).start();
    }

    private void stopScan() {
        isScanning = false;
        if (suProcess != null) {
            suProcess.destroy(); // Cierra el shell de root de forma segura
        }
        btnScan.setText("INICIAR ESCANEO TP-LINK");
        btnScan.setBackgroundColor(0xFF00FF00); // Cambia el botón de regreso a Verde
        scanProgress.setVisibility(View.GONE); // Oculta la barra de carga
    }

    // Agrega los routers de forma segura regresando los datos al hilo visual (UI Thread)
    private void agregarRedDesdeConsola(final String ssid, final String bssid, final String channel, final String vuln) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isScanning) {
                    networkList.add(new NetworkModel(ssid, bssid, channel, vuln));
                    adapter.notifyDataSetChanged(); // Avisa a la lista que dibuje la nueva tarjeta
                }
            }
        });
    }

    private void mostrarErrorEnUI() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "[❌] Requiere permisos de Superusuario o tarjeta compatible.", Toast.LENGTH_SHORT).show();
                stopScan();
            }
        });
    }
}
